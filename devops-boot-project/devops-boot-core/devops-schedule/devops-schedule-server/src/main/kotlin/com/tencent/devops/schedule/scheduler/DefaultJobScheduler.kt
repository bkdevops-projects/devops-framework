package com.tencent.devops.schedule.scheduler

import com.tencent.devops.schedule.api.WorkerRpcClient
import com.tencent.devops.schedule.config.ScheduleServerProperties
import com.tencent.devops.schedule.enums.BlockStrategyEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.enums.TriggerCodeEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.manager.WorkerManager
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.router.Routes
import com.tencent.devops.schedule.scheduler.monitor.JobRetryMonitor
import com.tencent.devops.schedule.scheduler.monitor.JobTodoMonitor
import com.tencent.devops.schedule.scheduler.monitor.WorkerStatusMonitor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import java.time.LocalDateTime
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 任务调度器默认实现类
 */
class DefaultJobScheduler(
    private val jobManager: JobManager,
    private val workerManager: WorkerManager,
    private val lockProvider: LockProvider,
    private val scheduleServerProperties: ScheduleServerProperties,
    private val workerRpcClient: WorkerRpcClient
) : JobScheduler, InitializingBean, DisposableBean {

    private lateinit var triggerThreadPool: ThreadPoolExecutor
    private lateinit var jobTodoMonitor: JobTodoMonitor
    private lateinit var jobRetryMonitor: JobRetryMonitor
    //private lateinit var jobLostMonitor: JobLostMonitor
    private lateinit var workerStatusMonitor: WorkerStatusMonitor

    override fun getJobManager() = jobManager
    override fun getWorkerManager() = workerManager

    override fun start() {
        triggerThreadPool = createThreadPool()
        jobTodoMonitor = JobTodoMonitor(this, lockProvider).apply { start() }
        jobRetryMonitor = JobRetryMonitor(this).apply { start() }
        // TODO 初版实现不去主动监控任务状态丢失的任务，触发成功则表示执行成功，执行结果由worker上报
        // jobLostMonitor = JobLostMonitor(this).apply { start() }
        workerStatusMonitor = WorkerStatusMonitor(this).apply { start() }
        logger.info("start upjob scheduler success")
    }

    override fun stop() {
        jobTodoMonitor.stop()
        jobRetryMonitor.stop()
        // jobLostMonitor.stop()
        workerStatusMonitor.stop()
        triggerThreadPool.shutdownNow()
        logger.info("shutdown job scheduler success")
    }

    override fun afterPropertiesSet() {
        this.start()
    }

    override fun destroy() {
        this.stop()
    }

    /**
     * 触发任务
     * @param jobId 任务id
     *
     */
    override fun trigger(
        jobId: String,
        triggerType: TriggerTypeEnum,
        retryCount: Int?,
        jobParam: String?,
        shardingParam: String?
    ) {
        logger.debug("prepare trigger job[$jobId]")
        triggerThreadPool.execute {
            try {
                val job = jobManager.findJobById(jobId) ?: run {
                    logger.warn("trigger job[$jobId] failed: job not exists.")
                    return@execute
                }
                if (job.triggerStatus == TriggerStatusEnum.STOP.code()) {
                    logger.warn("trigger job[$jobId] failed: job is stopped.")
                    return@execute
                }
                jobParam?.let { job.jobParam = it }
                val finalRetryCount = retryCount ?: job.maxRetryCount
                val group = workerManager.findGroupById(job.groupId) ?: run {
                    logger.warn("trigger job[$jobId] failed: group[${job.groupId}] not exists.")
                    return@execute
                }

                val pair = resolveShardingParam(shardingParam)
                if (pair == null && isShardingBroadcastJob(job, group)) {
                    repeat(group.registryList.size) {
                        processTrigger(job, group, triggerType, finalRetryCount, it, group.registryList.size)
                    }
                } else {
                    val index = pair?.first ?: 0
                    val total = pair?.second ?: 1
                    processTrigger(job, group, triggerType, finalRetryCount, index, total)
                }
            } catch (e: Exception) {
                logger.error("trigger job[$jobId] error: ${e.message}", e)
            }
        }
    }

    private fun isShardingBroadcastJob(job: JobInfo, group: WorkerGroup): Boolean {
        return job.routeStrategy == RouteStrategyEnum.SHARDING_BROADCAST.code() && group.registryList.isNotEmpty()
    }

    /**
     * 处理
     */
    private fun processTrigger(
        job: JobInfo,
        group: WorkerGroup,
        triggerType: TriggerTypeEnum,
        retryCount: Int,
        index: Int,
        total: Int
    ) {
        val blockStrategy = BlockStrategyEnum.ofCode(job.blockStrategy)
        val routeStrategy = RouteStrategyEnum.ofCode(job.routeStrategy)
        requireNotNull(blockStrategy)
        requireNotNull(routeStrategy)
        val shardingParam = if (routeStrategy == RouteStrategyEnum.SHARDING_BROADCAST) "$index/$total" else null

        // 1. 保存日志
        val jobLog = JobLog(
            jobId = job.id.orEmpty(),
            groupId = group.id.orEmpty(),
            triggerType = triggerType.code(),
            triggerTime = LocalDateTime.now()
        )
        val logId = jobManager.addJobLog(jobLog)
        // 2. 构造trigger param
        val triggerParam = TriggerParam(
            jobId = job.id.orEmpty(),
            jobHandler = job.jobHandler,
            jobParam = job.jobParam,
            blockStrategy = blockStrategy.code(),
            jobTimeout = job.jobTimeout,
            logId = logId,
            triggerTime = jobLog.triggerTime,
            broadcastIndex = index,
            broadcastTotal = total
        )
        // 3. 选择worker地址
        require(group.registryList.isNotEmpty()) { "没有可用的worker地址" }
        val address = when (routeStrategy) {
            RouteStrategyEnum.SHARDING_BROADCAST -> group.registryList[index]
            else -> Routes.route(routeStrategy, triggerParam, group.registryList)
        }
        // 4. 触发任务
        try {
            require(!address.isNullOrBlank()) { "没有可用的worker地址" }
            triggerParam.workerAddress = address
            val result = workerRpcClient.runJob(triggerParam)
            jobLog.triggerCode = result.code
            jobLog.triggerMsg = result.message
            jobLog.executionCode = ExecutionCodeEnum.RUNNING.code()
            logger.info("trigger job[${job.id}] success: $result")
        } catch (e: Exception) {
            logger.error("trigger job[${jobLog.jobId}] error: ${e.message}", e)
            jobLog.triggerCode = TriggerCodeEnum.FAILED.code()
            jobLog.triggerMsg = e.message
        }
        // 5. 保存结果
        jobLog.workerAddress = address
        jobLog.jobHandler = job.jobHandler
        jobLog.jobParam = job.jobParam
        jobLog.workerShardingParam = shardingParam
        jobLog.workerRetryCount = retryCount
        jobManager.updateJobLog(jobLog)
    }

    /**
     * 创建任务trigger调度线程池
     */
    private fun createThreadPool(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            10,
            scheduleServerProperties.maxTriggerPoolSize,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(1000)
        ) { runnable ->
            Thread(runnable, "job-trigger-${runnable.hashCode()}")
        }
    }

    private fun resolveShardingParam(value: String?): Pair<Int, Int>? {
        if (value == null) {
            return null
        }
        val parts = value.split("/")
        return try {
            if (parts.size == 2) {
                Pair(parts[0].toInt(), parts[1].toInt())
            } else null
        } catch (ignored: Exception) {
            null
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultJobScheduler::class.java)
    }
}

