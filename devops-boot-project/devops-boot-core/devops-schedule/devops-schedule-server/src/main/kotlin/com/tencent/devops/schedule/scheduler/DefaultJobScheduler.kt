package com.tencent.devops.schedule.scheduler

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.tencent.devops.schedule.api.WorkerRpcClient
import com.tencent.devops.schedule.config.ScheduleServerProperties
import com.tencent.devops.schedule.enums.BlockStrategyEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.enums.TriggerCodeEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.manager.WorkerManager
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.router.Routes
import com.tencent.devops.schedule.scheduler.event.JobTriggerEvent
import com.tencent.devops.schedule.scheduler.monitor.JobRetryMonitor
import com.tencent.devops.schedule.scheduler.monitor.JobTodoMonitor
import com.tencent.devops.schedule.scheduler.monitor.WorkerStatusMonitor
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.LinkedBlockingDeque
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
    private val workerRpcClient: WorkerRpcClient,
    private val registry: MeterRegistry,
    private val publisher: ApplicationEventPublisher,
) : JobScheduler, InitializingBean, DisposableBean {

    private lateinit var triggerThreadPool: ThreadPoolExecutor
    private lateinit var jobTodoMonitor: JobTodoMonitor
    private lateinit var jobRetryMonitor: JobRetryMonitor

    // private lateinit var jobLostMonitor: JobLostMonitor
    private lateinit var workerStatusMonitor: WorkerStatusMonitor
    private var cc = JobCongestionControl(scheduleServerProperties.maxScheduleLatencyMillis)

    private var workGroupCache: LoadingCache<String, WorkerGroup?> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build(CacheLoader.from { groupId -> groupId?.let { workerManager.findGroupById(it) } })

    override fun getJobManager() = jobManager
    override fun getWorkerManager() = workerManager

    override fun start() {
        triggerThreadPool = createThreadPool()
        ExecutorServiceMetrics(triggerThreadPool, "triggerThreadPool", emptyList()).bindTo(registry)
        jobTodoMonitor = JobTodoMonitor(this, lockProvider, publisher, this::determineLoadCount).apply { start() }
        jobRetryMonitor = JobRetryMonitor(this).apply { start() }
        // TODO 初版实现不去主动监控任务状态丢失的任务，触发成功则表示执行成功，执行结果由worker上报
        // jobLostMonitor = JobLostMonitor(this).apply { start() }
        workerStatusMonitor = WorkerStatusMonitor(this).apply { start() }
        monitor()
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
        triggerContext: JobTriggerContext,
        triggerType: TriggerTypeEnum,
        retryCount: Int?,
        jobParam: String?,
        shardingParam: String?,
    ) {
        val jobId = triggerContext.job.id.orEmpty()
        logger.debug("prepare trigger job[{}]", triggerContext)
        triggerThreadPool.execute {
            val startTime = System.currentTimeMillis()
            val fireTime = triggerContext.scheduledFireTime ?: triggerContext.fireTime
            try {
                val job = triggerContext.job
                jobParam?.let { job.jobParam = it }
                val finalRetryCount = retryCount ?: job.maxRetryCount
                val group = workGroupCache.get(job.groupId) ?: run {
                    throw IllegalArgumentException("group[${job.groupId}] not exists.")
                }

                val pair = resolveShardingParam(shardingParam)
                if (pair == null && isShardingBroadcastJob(job, group)) {
                    repeat(group.registryList.size) {
                        processTrigger(triggerContext, group, triggerType, finalRetryCount, it, group.registryList.size)
                    }
                } else {
                    val index = pair?.first ?: 0
                    val total = pair?.second ?: 1
                    processTrigger(triggerContext, group, triggerType, finalRetryCount, index, total)
                }
            } catch (e: Exception) {
                logger.error("trigger job[$jobId] error: ${e.message}", e)
            } finally {
                val triggerEvent = JobTriggerEvent.create(
                    jobId,
                    startTime,
                    System.currentTimeMillis(),
                    fireTime,
                )
                publisher.publishEvent(triggerEvent)
                cc.updateLatency(System.currentTimeMillis() - fireTime)
            }
        }
    }

    override fun trigger(
        jobId: String,
        triggerType: TriggerTypeEnum,
        retryCount: Int?,
        jobParam: String?,
        shardingParam: String?,
    ) {
        val job = jobManager.findJobById(jobId) ?: throw IllegalArgumentException("No job $jobId")
        val triggerContext = JobTriggerContext(
            job = job,
            fireTime = System.currentTimeMillis(),
        )
        trigger(triggerContext, triggerType, retryCount, jobParam, shardingParam)
    }

    private fun isShardingBroadcastJob(job: JobInfo, group: WorkerGroup): Boolean {
        return job.routeStrategy == RouteStrategyEnum.SHARDING_BROADCAST.code() && group.registryList.isNotEmpty()
    }

    /**
     * 处理
     */
    private fun processTrigger(
        triggerContext: JobTriggerContext,
        group: WorkerGroup,
        triggerType: TriggerTypeEnum,
        retryCount: Int,
        index: Int,
        total: Int,
    ) {
        val job = triggerContext.job
        val blockStrategy = BlockStrategyEnum.ofCode(job.blockStrategy)
        val routeStrategy = RouteStrategyEnum.ofCode(job.routeStrategy)
        requireNotNull(blockStrategy)
        requireNotNull(routeStrategy)
        val shardingParam = if (routeStrategy == RouteStrategyEnum.SHARDING_BROADCAST) "$index/$total" else null

        // 1. 保存日志
        val fireTime = triggerContext.scheduledFireTime ?: triggerContext.fireTime
        val jobLog = JobLog(
            jobId = job.id.orEmpty(),
            groupId = group.id.orEmpty(),
            triggerType = triggerType.code(),
            triggerTime = LocalDateTime.now(),
            scheduledFireTime = Instant.ofEpochMilli(fireTime).atZone(ZoneId.systemDefault()).toLocalDateTime(),
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
            scheduledFireTime = jobLog.scheduledFireTime,
            broadcastIndex = index,
            broadcastTotal = total,
            updateTime = job.updateTime,
            source = job.source,
            image = job.image,
            jobMode = job.jobMode,
            command = job.command,
            cmdFileName = job.cmdFileName,
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
            if (result.code == TriggerCodeEnum.SUCCESS.code()) {
                jobLog.executionCode = ExecutionCodeEnum.RUNNING.code()
                logger.info("trigger job[${job.id}] success: $result")
            } else {
                logger.info("trigger job[${job.id}] failed: $result")
            }
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
        // 队列不能太长，如果下游延迟加大，会导致队列中的所有任务调度延迟都加大
        return ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            scheduleServerProperties.maxTriggerPoolSize,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(scheduleServerProperties.maxTriggerQueueSize),
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
            } else {
                null
            }
        } catch (ignored: Exception) {
            null
        }
    }

    private fun monitor() {
        Gauge.builder("devops.schedule.server.trigger.load", this::determineLoadCount)
            .description("加载任务数")
            .baseUnit("TASK")
            .register(registry)
    }

    private fun determineLoadCount(): Int {
        val availableThreads = if (triggerThreadPool.poolSize == 0) {
            // 线程池还未提交任务
            triggerThreadPool.corePoolSize
        } else {
            triggerThreadPool.poolSize - triggerThreadPool.activeCount
        }
        return maxOf(availableThreads, cc.getCwnd())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultJobScheduler::class.java)
    }
}
