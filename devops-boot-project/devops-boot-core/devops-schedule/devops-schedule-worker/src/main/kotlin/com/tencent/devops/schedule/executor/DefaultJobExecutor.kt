package com.tencent.devops.schedule.executor

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.config.ScheduleWorkerProperties
import com.tencent.devops.schedule.enums.BlockStrategyEnum
import com.tencent.devops.schedule.enums.JobModeEnum
import com.tencent.devops.schedule.enums.JobModeEnum.BEAN
import com.tencent.devops.schedule.enums.JobModeEnum.K8S_SHELL
import com.tencent.devops.schedule.enums.JobModeEnum.SHELL
import com.tencent.devops.schedule.handler.K8sShellHandler
import com.tencent.devops.schedule.handler.ShellHandler
import com.tencent.devops.schedule.k8s.K8sHelper
import com.tencent.devops.schedule.pojo.ScheduleResponse
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.schedule.thread.JobThread
import com.tencent.devops.schedule.thread.JobThreadGroup
import com.tencent.devops.schedule.thread.TriggerTask
import com.tencent.devops.web.util.SpringContextHolder
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.ConcurrentHashMap

/**
 * JobExecutor默认实现
 */
class DefaultJobExecutor(
    private val workerProperties: ScheduleWorkerProperties,
    private val serverRpcClient: ServerRpcClient,
) : JobExecutor, DisposableBean {

    private val k8sShellHandler: K8sShellHandler by lazy { createK8sHandler() }
    private val shellHandler: ShellHandler = ShellHandler(workerProperties.sourcePath)
    private val threadGroup = JobThreadGroup(workerProperties.executor.threads, serverRpcClient)
    private val jobThreadRepository = ConcurrentHashMap<String, JobThread>()

    override fun execute(param: TriggerParam): ScheduleResponse {
        val jobId = param.jobId
        val logId = param.logId
        logger.debug("prepare to execute job[$jobId], log[$logId]: {}", param)
        require(param.jobId.isNotBlank())
        require(param.logId.isNotBlank())
        require(param.jobParam.isNotBlank())

        // 根据job的类型，选择不同的执行方式
        val jobMode = param.jobMode
        val handler = when (JobModeEnum.ofCode(jobMode)) {
            BEAN -> {
                try {
                    SpringContextHolder.getBean(JobHandler::class.java, param.jobHandler)
                } catch (e: BeansException) {
                    throw RuntimeException("未找到jobHandler[${param.jobHandler}]")
                }
            }

            SHELL -> shellHandler

            K8S_SHELL -> k8sShellHandler

            else -> {
                // 不支持任务类型
                throw IllegalArgumentException("Job mode [$jobMode] not supported")
            }
        }
        var jobThread = loadJobThread(jobId)
        if (jobThread != null) {
            val blockStrategy = BlockStrategyEnum.ofCode(param.blockStrategy)
            when (blockStrategy) {
                BlockStrategyEnum.DISCARD_LATER -> {
                    if (jobThread.hasRunningJobs(jobId)) {
                        return ScheduleResponse.failed("discard by block strategy")
                    }
                }

                BlockStrategyEnum.COVER_EARLY -> {
                    if (jobThread.hasRunningJobs(jobId)) {
                        jobThread.removeEarlyJob(jobId)
                    }
                }

                else -> {
                    // 入队
                }
            }
        } else {
            jobThread = registerJobThread(jobId)
        }
        val task = TriggerTask(jobId, handler, param)
        jobThread.pushTriggerQueue(task)
        return ScheduleResponse.success()
    }

    override fun destroy() {
        logger.info("Destroying DefaultJobExecutor")
        threadGroup.close()
        jobThreadRepository.clear()
    }

    private fun registerJobThread(jobId: String): JobThread {
        return jobThreadRepository.computeIfAbsent(jobId) { threadGroup.next() }
    }

    private fun loadJobThread(jobId: String): JobThread? {
        return jobThreadRepository[jobId]
    }

    private fun createK8sHandler(): K8sShellHandler {
        val k8sProperties = workerProperties.k8s
        val k8sClient = K8sHelper.createClient(k8sProperties)
        return K8sShellHandler(k8sClient, k8sProperties.namespace, k8sProperties.limit)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultJobExecutor::class.java)
    }
}
