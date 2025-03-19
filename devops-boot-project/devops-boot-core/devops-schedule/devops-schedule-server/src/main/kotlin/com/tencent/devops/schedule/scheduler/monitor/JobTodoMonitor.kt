package com.tencent.devops.schedule.scheduler.monitor

import com.tencent.devops.schedule.constants.JOB_LOAD_LOCK_KEY
import com.tencent.devops.schedule.constants.PRE_LOAD_TIME
import com.tencent.devops.schedule.enums.MisfireStrategyEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum.CRON
import com.tencent.devops.schedule.enums.TriggerTypeEnum.MISFIRE
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.scheduler.JobTriggerContext
import com.tencent.devops.schedule.scheduler.event.JobMisfireEvent
import com.tencent.devops.schedule.utils.alignTime
import com.tencent.devops.schedule.utils.computeNextTriggerTime
import com.tencent.devops.schedule.utils.sleep
import com.tencent.devops.schedule.utils.terminate
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

/**
 * 任务加载器
 */
open class JobTodoMonitor(
    private val jobScheduler: JobScheduler,
    private val lockProvider: LockProvider,
    private val publisher: ApplicationEventPublisher,
    private val loadCountFunction: () -> Int,
) {
    private val jobManager = jobScheduler.getJobManager()

    private val timeRingMap: MutableMap<Int, MutableList<JobTriggerContext>> = ConcurrentHashMap()
    private lateinit var loaderThread: Thread
    private lateinit var timeRingThread: Thread
    private var loaderThreadRunning = true
    private var timeRingThreadRunning = true
    fun start() {
        loaderThread = thread(
            isDaemon = true,
            name = "job-todo-loader-thread",
        ) {
            alignTime(PRE_LOAD_TIME)
            while (loaderThreadRunning) {
                val start = System.currentTimeMillis()
                val loads = loadJobs()
                val elapsed = System.currentTimeMillis() - start
                if (elapsed < 1000) {
                    alignTime(if (loads > 0) 1000 else PRE_LOAD_TIME)
                }
            }
        }
        timeRingThread = thread(
            isDaemon = true,
            name = "job-time-ring-thread",
        ) {
            while (timeRingThreadRunning) {
                alignTime(1000)
                triggerTimeRingJobs()
            }
        }
        logger.info("startup job todo monitor success")
    }

    fun stop() {
        loaderThreadRunning = false
        sleep(1)
        terminate(loaderThread)

        var hasRingData = false
        if (timeRingMap.isNotEmpty()) {
            for (second in timeRingMap.keys) {
                if (timeRingMap[second]?.isNotEmpty() == true) {
                    hasRingData = true
                    break
                }
            }
        }
        if (hasRingData) {
            sleep(8)
        }
        timeRingThreadRunning = false
        terminate(timeRingThread)
        logger.info("shutdown job todo monitor success")
    }

    private fun loadJobs(): Int {
        var count = 0
        var lockToken: String? = null
        try {
            logger.trace("start to load jobs")
            // 加锁
            lockToken = lockProvider.acquire(JOB_LOAD_LOCK_KEY, PRE_LOAD_TIME)
            if (lockToken == null) {
                return 0
            }
            val jobs = jobManager.findTodoJobs(System.currentTimeMillis() + PRE_LOAD_TIME, loadCountFunction())
            count = jobs.size
            for (job in jobs) {
                // 每次都获取新的当前时间戳，可以提高任务精度
                val now = System.currentTimeMillis()
                val nextTriggerTime = job.nextTriggerTime
                if (nextTriggerTime < now - PRE_LOAD_TIME) {
                    // 超出执行时间，任务过期
                    handleMisfireJob(job)
                } else if (nextTriggerTime <= now) {
                    // 未超出执行时间，立即触发，计算延迟触发时长
                    triggerJob(job)
                } else {
                    // 还未到执行时间，time-ring 触发
                    pushTimeRingJob(job)
                }
            }
            jobs.forEach {
                jobManager.updateJobSchedule(it)
            }
        } catch (e: Exception) {
            logger.error("load jobs failed: $e", e)
        } finally {
            lockToken?.let {
                lockProvider.release(JOB_LOAD_LOCK_KEY, it)
            }
        }
        logger.debug("finish load jobs, $count jobs loaded")
        return count
    }

    private fun triggerTimeRingJobs() {
        try {
            val start = System.currentTimeMillis()
            val second = LocalDateTime.now().second
            logger.trace("Second $second")
            // 避免调度时间过长，导致任务丢失，所以这里向前跨度处理2秒
            val jobs = mutableListOf<JobTriggerContext>()
            repeat(TICK_RANGE) {
                jobs.addAll(timeRingMap.remove((second + 60 - it) % 60).orEmpty())
            }
            jobs.forEach {
                jobScheduler.trigger(it, CRON)
            }
            val elapsed = System.currentTimeMillis() - start
            if (elapsed > TICK_RANGE * 1000) {
                logger.warn("Too much tasks at slot $second")
            }
            logger.trace("Second $second completed,trigger ${jobs.size} jobs, elapsed $elapsed ms.")
            jobs.clear()
        } catch (e: Exception) {
            logger.error("trigger time ring jobs failed: $e", e)
        }
    }

    /**
     * 处理过期任务
     */
    private fun handleMisfireJob(job: JobInfo) {
        val triggerContext = triggerFired(job)
        when (MisfireStrategyEnum.ofCode(job.misfireStrategy)) {
            MisfireStrategyEnum.RETRY -> {
                logger.warn("${job.id} is misfire, retry")
                jobScheduler.trigger(triggerContext, MISFIRE)
            }

            MisfireStrategyEnum.IGNORE -> {
                logger.warn("${job.id} is misfire, ignore")
                // do nothing
            }

            else -> {
                throw RuntimeException("misfire strategy is illegal")
            }
        }
        publisher.publishEvent(JobMisfireEvent(job.id.orEmpty()))
    }

    /**
     * 触发任务
     */
    private fun triggerJob(job: JobInfo) {
        val triggerContext = triggerFired(job)
        jobScheduler.trigger(triggerContext, CRON)
        if (job.triggerStatus == TriggerStatusEnum.RUNNING.code() && job.nextTriggerTime <= System.currentTimeMillis() + PRE_LOAD_TIME) {
            pushTimeRingJob(job)
        }
    }

    /**
     * 保存time-ring任务
     */
    private fun pushTimeRingJob(job: JobInfo) {
        val ringSecond = (job.nextTriggerTime / 1000 % 60).toInt()
        val from = Instant.ofEpochMilli(job.nextTriggerTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val triggerContext = triggerFired(job, from)
        val timeRingItem = timeRingMap.computeIfAbsent(ringSecond) { mutableListOf() }
        timeRingItem.add(triggerContext)
    }

    private fun generateNextTriggerTime(job: JobInfo, from: LocalDateTime = LocalDateTime.now()) {
        val nextTriggerTime = computeNextTriggerTime(job, from)
        if (nextTriggerTime == null) {
            job.triggerStatus = TriggerStatusEnum.STOP.code()
            job.lastTriggerTime = 0
            job.nextTriggerTime = 0
            logger.warn(
                "failed to generate next trigger time for job[${job.id}]: scheduleType={${job.scheduleType}}, " +
                    "scheduleConf={${job.scheduleConf}}",
            )
            return
        }
        job.lastTriggerTime = job.nextTriggerTime
        job.nextTriggerTime = nextTriggerTime
    }

    private fun triggerFired(job: JobInfo, from: LocalDateTime = LocalDateTime.now()): JobTriggerContext {
        val prevFireTime = job.lastTriggerTime
        generateNextTriggerTime(job, from)
        return JobTriggerContext(
            job = job,
            fireTime = System.currentTimeMillis(),
            prevFireTime = prevFireTime,
            nextFireTime = job.nextTriggerTime,
            scheduledFireTime = job.lastTriggerTime,
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobTodoMonitor::class.java)
        private const val TICK_RANGE = 3 // 时间轮处理任务范围，这里的3表示，每次可以处理3个刻度，即过去2s到当前这一秒
    }
}
