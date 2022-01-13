package com.tencent.devops.schedule.scheduler.monitor

import com.tencent.devops.schedule.enums.AlarmStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.utils.sleep
import com.tencent.devops.schedule.utils.terminate
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

/**
 * 任务重试管理类
 */
open class JobRetryMonitor(
    private val jobScheduler: JobScheduler
) {
    private val jobManager = jobScheduler.getJobManager()

    private lateinit var monitorThread: Thread
    private var running = true

    fun start() {
        monitorThread = thread(
            isDaemon = true,
            name = "job-retry-monitor-thread"
        ) {
            while (running) {
                refresh()
                sleep(10)
            }
        }
        logger.info("startup job retry monitor success")
    }

    fun stop() {
        running = false
        terminate(monitorThread)
    }

    private fun refresh() {
        try {
            val failedLogIds = jobManager.findFailJobLogIds(BATCH_SIZE)
            for(logId in failedLogIds) {
                // lock log
                val modifyCount = jobManager.updateAlarmStatus(
                    logId = logId,
                    old = AlarmStatusEnum.TODO.code(),
                    new = AlarmStatusEnum.LOCKED.code()
                )
                if (modifyCount < 1) {
                   continue
                }
                val log = jobManager.findJobLogById(logId)!!
                val job = jobManager.findJobById(log.jobId)
                val retryCount = log.workerRetryCount ?: 0
                if (retryCount > 0) {
                    jobScheduler.trigger(
                        jobId = log.jobId,
                        triggerType = TriggerTypeEnum.RETRY,
                        retryCount = retryCount - 1,
                        shardingParam = log.workerShardingParam,
                        jobParam = log.jobParam
                    )
                    jobManager.updateJobLog(log)
                }
                val newAlarmStatus = if (job != null) {
                    // TODO 发送告警
                    logger.info("alarm success")
                    AlarmStatusEnum.SUCCESS
                } else {
                    AlarmStatusEnum.IGNORED
                }
                jobManager.updateAlarmStatus(
                    logId = logId,
                    old = AlarmStatusEnum.LOCKED.code(),
                    new = newAlarmStatus.code()
                )
            }
        } catch (e: Exception) {
            if (running) {
                logger.error("job retry monitor occur error: ${e.message}", e)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobRetryMonitor::class.java)

        private const val BATCH_SIZE = 1000
    }
}
