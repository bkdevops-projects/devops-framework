package com.tencent.devops.schedule.manager

import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.enums.WorkerStatusEnum
import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.pojo.worker.WorkerGroupCreateRequest
import com.tencent.devops.schedule.pojo.worker.WorkerGroupName
import com.tencent.devops.schedule.pojo.worker.WorkerGroupQueryParam
import com.tencent.devops.schedule.pojo.worker.WorkerInfo
import com.tencent.devops.schedule.provider.WorkerProvider
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * WorkerManager默认实现类
 */
open class DefaultWorkerManager(
    private val workerProvider: WorkerProvider
) : WorkerManager {

    override fun listGroupPage(param: WorkerGroupQueryParam): Page<WorkerGroup> {
        return workerProvider.listGroupPage(param)
    }

    override fun listGroupName(): List<WorkerGroupName> {
        return workerProvider.listGroupName()
    }

    override fun findGroupById(id: String): WorkerGroup? {
        return workerProvider.findGroupById(id)
    }

    override fun createGroup(request: WorkerGroupCreateRequest): String {
        with(request) {
            require(name.isNotBlank())
            requireNotNull(DiscoveryTypeEnum.ofCode(discoveryType))
            // 验证name唯一
            require(workerProvider.findGroupByName(name) == null) { "group[$name]已存在" }
            val workerGroup = WorkerGroup(
                name = name,
                discoveryType = discoveryType,
                updateTime = LocalDateTime.now(),
                registryList = emptyList()
            )
            return workerProvider.addGroup(workerGroup).apply {
                logger.info("create worker[${name}] group success")
            }
        }
    }

    override fun updateGroup(group: WorkerGroup) {
        workerProvider.updateGroup(group)
    }

    override fun workerHeartbeat(param: HeartBeatParam) {
        val address = param.address.trim().trimEnd('/')
        val group = param.group.trim()
        require(address.isNotBlank())
        require(group.isNotBlank())
        val status = WorkerStatusEnum.ofCode(param.status)
        when(status) {
            WorkerStatusEnum.RUNNING -> {
                workerProvider.upsertWorker(group, address)
            }
            WorkerStatusEnum.STOP -> {
                workerProvider.deleteWorker(group, address)
            }
        }
        logger.debug("worker[$group-$address] heartbeat, status[$status]")
    }

    override fun deleteWorkerGroup(id: String) {
        workerProvider.deleteWorkerGroup(id)
    }

    override fun listGroupByDiscoverType(type: DiscoveryTypeEnum): List<WorkerGroup> {
        return workerProvider.listGroupByDiscoveryType(type.code())
    }

    override fun deleteDeadWorker(timeout: Int, from: LocalDateTime) {
        val time = from.minusSeconds(timeout.toLong())
        val affected = workerProvider.deleteWorkerByUpdateTimeLessThan(time)
        if (affected > 0) {
            logger.info("delete $affected dead workers")
        }
    }

    override fun listWorkerByUpdateTime(timeout: Int, from: LocalDateTime): List<WorkerInfo> {
        val time = from.minusSeconds(timeout.toLong())
        return workerProvider.listWorkerByUpdateTimeGreaterThan(time)
    }



    companion object {
        private val logger = LoggerFactory.getLogger(DefaultWorkerManager::class.java)
    }
}
