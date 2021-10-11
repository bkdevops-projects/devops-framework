package com.tencent.devops.schedule.scheduler.monitor

import com.tencent.devops.schedule.constants.JOB_LOAD_LOCK_KEY
import com.tencent.devops.schedule.constants.PRE_LOAD_TIME
import com.tencent.devops.schedule.enums.MisfireStrategyEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.utils.alignTime
import com.tencent.devops.schedule.utils.computeNextTriggerTime
import com.tencent.devops.schedule.utils.sleep
import com.tencent.devops.schedule.utils.terminate
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * 任务加载器
 */
open class JobTodoMonitor(
    private val jobScheduler: JobScheduler,
    private val lockProvider: LockProvider
) {
    private val jobManager = jobScheduler.getJobManager()

    private val timeRingMap: MutableMap<Int, MutableList<String>> = ConcurrentHashMap()
    private lateinit var loaderThread: Thread
    private lateinit var timeRingThread: Thread
    private var loaderThreadRunning = true
    private var timeRingThreadRunning = true

    fun start() {
        loaderThread = thread(
            isDaemon = true,
            name = "job-todo-loader-thread"
        ) {
            alignTime(PRE_LOAD_TIME)
            while (loaderThreadRunning) {
                val elapsed = measureTimeMillis { loadJobs() }
                if (elapsed < 1000) {
                    alignTime(PRE_LOAD_TIME)
                }
            }
        }
        timeRingThread = thread(
            isDaemon = true,
            name = "job-time-ring-thread"
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
                if(timeRingMap[second]?.isNotEmpty() == true) {
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
                return count
            }
            val now = System.currentTimeMillis()
            val jobs = jobManager.findTodoJobs(now + PRE_LOAD_TIME)
            count = jobs.size
            for (job in jobs) {
                val nextTriggerTime = job.nextTriggerTime
                if (nextTriggerTime < now - PRE_LOAD_TIME) {
                    // 超出执行时间，任务过期
                    handleMisfireJob(job)
                } else if (nextTriggerTime <= now) {
                    // 未超出执行时间，立即触发，计算延迟触发时长
                    triggerJob(job, now)
                } else {
                    // 还未到执行时间，time-ring 触发
                    pushTimeRingJob(job)
                }
                jobManager.updateJobSchedule(job)
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
            val second = LocalDateTime.now().second
            // 向前处理2秒
            val jobs = mutableListOf<String>()
            repeat(3) {
                jobs.addAll(timeRingMap.remove((second + 60 - it) % 60).orEmpty())
            }
            jobs.forEach {
                jobScheduler.trigger(it, TriggerTypeEnum.CRON)
            }
            jobs.clear()
        } catch (e: Exception) {
            logger.error("trigger time ring jobs failed: $e", e)
        }
    }

    /**
     * 处理过期任务
     */
    private fun handleMisfireJob(job: JobInfo) {
        when (MisfireStrategyEnum.ofCode(job.misfireStrategy)) {
            MisfireStrategyEnum.RETRY -> {
                logger.warn("job[${job.id}] is misfire, retry")
                jobScheduler.trigger(job.id.orEmpty(), TriggerTypeEnum.MISFIRE)
            }
            MisfireStrategyEnum.IGNORE -> {
                logger.warn("job[${job.id}] is misfire, ignore")
                // do nothing
            }
        }
        // fresh next
        generateNextTriggerTime(job)
    }

    /**
     * 触发任务
     */
    private fun triggerJob(job: JobInfo, now: Long) {
        jobScheduler.trigger(job.id.orEmpty(), TriggerTypeEnum.CRON)
        generateNextTriggerTime(job)
        if (job.triggerStatus == TriggerStatusEnum.RUNNING.code() && job.nextTriggerTime <= now + PRE_LOAD_TIME) {
            pushTimeRingJob(job)
        }
    }

    /**
     * 保存time-ring任务
     */
    private fun pushTimeRingJob(job: JobInfo) {
        val ringSecond = (job.nextTriggerTime / 1000 % 60).toInt()
        val timeRingItem = timeRingMap.computeIfAbsent(ringSecond) { mutableListOf() }
        timeRingItem.add(job.id.orEmpty())
        val from = Instant.ofEpochMilli(job.nextTriggerTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
        generateNextTriggerTime(job, from)
    }

    private fun generateNextTriggerTime(job: JobInfo, from: LocalDateTime = LocalDateTime.now()) {
        val nextTriggerTime = computeNextTriggerTime(job, from)
        if (nextTriggerTime == null) {
            job.triggerStatus = TriggerStatusEnum.STOP.code()
            job.lastTriggerTime = 0
            job.nextTriggerTime = 0
            logger.warn(
                "failed to generate next trigger time for job[${job.id}]: scheduleType={${job.scheduleType}}, " +
                        "scheduleConf={${job.scheduleConf}}"
            )
            return
        }
        job.lastTriggerTime = job.nextTriggerTime
        job.nextTriggerTime = nextTriggerTime
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobTodoMonitor::class.java)
    }
}
