package com.tencent.devops.schedule.scheduler.monitor

import com.tencent.devops.schedule.constants.WORKER_BEAT_PERIOD
import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.utils.sleep
import com.tencent.devops.schedule.utils.terminate
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

/**
 * worker状态监控类
 */
open class WorkerStatusMonitor(
    jobScheduler: JobScheduler
) {
    private val workerManager = jobScheduler.getWorkerManager()

    private lateinit var monitorThread: Thread
    private var running = true

    fun start() {
        monitorThread = thread(
            isDaemon = true,
            name = "worker-status-monitor-thread"
        ) {
            while (running) {
                refresh()
                sleep(WORKER_BEAT_PERIOD)
            }
        }
        logger.info("startup worker status monitor success")
    }

    fun stop() {
        running = false
        terminate(monitorThread)
    }

    private fun refresh() {
        try {
            val groups = workerManager.listGroupByDiscoverType(DiscoveryTypeEnum.AUTO)
            if (groups.isEmpty()) {
                return
            }
            // remove dead worker
            workerManager.deleteDeadWorker(DEAD_TIMEOUT, LocalDateTime.now())
            // fresh online worker
            val groupWorkerMap = HashMap<String, MutableSet<String>>()
            val workers = workerManager.listWorkerByUpdateTime(DEAD_TIMEOUT, LocalDateTime.now())
            workers.forEach { worker ->
                val workerSet = groupWorkerMap.computeIfAbsent(worker.group) { mutableSetOf() }
                workerSet.add(worker.address)
            }
            // fresh group address
            groups.forEach { group ->
                group.registryList = groupWorkerMap[group.name].orEmpty().toList()
                workerManager.updateGroup(group)
            }
            logger.debug("finish update worker group status")
        } catch (e: Exception) {
            if (running) {
                logger.error("worker status monitor occur error: ${e.message}", e)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorkerStatusMonitor::class.java)
        private const val DEAD_TIMEOUT = WORKER_BEAT_PERIOD * 3
    }
}
