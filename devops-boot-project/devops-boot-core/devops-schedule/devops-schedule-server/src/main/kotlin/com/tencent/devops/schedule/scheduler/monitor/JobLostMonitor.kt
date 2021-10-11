package com.tencent.devops.schedule.scheduler.monitor

import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.utils.sleep
import com.tencent.devops.schedule.utils.terminate
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

/**
 * 任务运行完成状态监控类
 * 当任务执行时间超过指定时间(默认30min),且对应的worker不在线，则主动标记任务失败
 */
open class JobLostMonitor(
    jobScheduler: JobScheduler
) {
    private val jobManager = jobScheduler.getJobManager()
    private val workerManager = jobScheduler.getWorkerManager()

    private lateinit var monitorThread: Thread
    private var running = true

    fun start() {
        monitorThread = thread(
            isDaemon = true,
            name = "job-lost-monitor-thread"
        ) {
            while (running) {
                sleep(60)
                refresh()
            }
        }
        logger.info("startup job lost monitor success")
    }

    fun stop() {
        running = false
        terminate(monitorThread)
    }

    private fun refresh() {
        try {
            val time = LocalDateTime.now().minusMinutes(10)
            val lostLogIds = jobManager.findLostJobLogIds(time)
            for(logId in lostLogIds) {
                val log = jobManager.findJobLogById(logId)!!
                val group = workerManager.findGroupById(log.groupId)
                if (group == null) {
                    val executionCode = ExecutionCodeEnum.FAILED.code()
                    jobManager.completeJob(logId, executionCode, "任务结果丢失，标记失败")
                    continue
                }

                // TODO: 查询worker状态
                when(DiscoveryTypeEnum.ofCode(group.discoveryType)) {
                    DiscoveryTypeEnum.CLOUD -> {}
                    else -> {}
                }

                val executionCode = ExecutionCodeEnum.FAILED.code()
                jobManager.completeJob(logId, executionCode, "任务结果丢失，标记失败")
            }
        } catch (e: Exception) {
            if (running) {
                logger.error("job lost monitor occur error: ${e.message}", e)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobLostMonitor::class.java)
    }
}
