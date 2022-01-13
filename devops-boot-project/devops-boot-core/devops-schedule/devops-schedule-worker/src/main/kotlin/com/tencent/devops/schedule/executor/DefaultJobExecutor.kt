package com.tencent.devops.schedule.executor

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.config.ScheduleWorkerProperties
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.utils.jackson.readJsonString
import com.tencent.devops.web.util.SpringContextHolder
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * JobExecutor默认实现
 */
class DefaultJobExecutor(
    private val workerProperties: ScheduleWorkerProperties,
    private val serverRpcClient: ServerRpcClient
) : JobExecutor {

    /**
     * 任务执行线程池
     */
    private val threadPool = createThreadPool()

    override fun execute(param: TriggerParam) {
        val jobId = param.jobId
        val logId = param.logId
        logger.debug("prepare to execute job[$jobId], log[$logId]: $param")
        require(param.jobHandler.isNotBlank())
        require(param.jobId.isNotBlank())
        require(param.logId.isNotBlank())
        require(param.jobParam.isNotBlank())
        val context = buildJobContext(param)
        // 寻找jobHandler bean
        val handler = try {
            SpringContextHolder.getBean(JobHandler::class.java, param.jobHandler)
        } catch (e: BeansException) {
            throw RuntimeException("未找到jobHandler[${param.jobHandler}]")
        }
        threadPool.submit {
            // 执行任务逻辑，获取结果
            val result = try {
                handler.execute(context)
            } catch (e: Throwable) {
                logger.error("execute job log[$logId] error: ${e.message}", e)
                JobExecutionResult.failed(e.message.orEmpty())
            }
            result.logId = logId
            logger.info("complete job log[$logId]: $result")
            // 上报任务结果
            try {
                serverRpcClient.submitResult(result)
                logger.info("submit job log[$logId] result success")
            } catch (e: Exception) {
                logger.error("submit job log[$logId] result error: ${e.message}", e)
            }
        }
    }

    private fun buildJobContext(param: TriggerParam): JobContext {
        with(param) {
            return JobContext(
                jobId = jobId,
                jobParamMap = jobParam.readJsonString(),
                logId = logId,
                triggerTime = triggerTime,
                broadcastIndex = broadcastIndex,
                broadcastTotal = broadcastTotal
            )
        }
    }

    private fun createThreadPool(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            workerProperties.executor.corePoolSize,
            workerProperties.executor.maximumPoolSize,
            workerProperties.executor.keepAliveTime,
            TimeUnit.SECONDS,
            ArrayBlockingQueue(1024)
        ) { runnable ->
            Thread(runnable, "job-executor-${runnable.hashCode()}")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultJobExecutor::class.java)
    }

}
