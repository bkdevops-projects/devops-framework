package com.tencent.devops.schedule.discovery

import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.manager.WorkerManager
import com.tencent.devops.schedule.utils.sleep
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.discovery.event.HeartbeatEvent
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent
import org.springframework.cloud.client.discovery.event.ParentHeartbeatEvent
import org.springframework.context.event.EventListener

/**
 * 基于时间监听实现worker自动发现
 */
open class WorkerDiscoveryListener(
    private val discoveryClient: DiscoveryClient,
    private val workerManager: WorkerManager
) {
    private val monitor: HeartbeatMonitor = HeartbeatMonitor()

    @EventListener
    fun onApplicationReady(event: ApplicationReadyEvent) {
        // 启动完成后执行一次
        discover()
    }

    @EventListener
    fun onInstanceRegistered(event: InstanceRegisteredEvent<*>) {
        // 自己注册不需执行
        // discover()
    }

    @EventListener
    fun onParentHeartbeat(event: ParentHeartbeatEvent) {
        discoverIfNeeded(event.value)
    }

    @EventListener
    fun onApplicationEvent(event: HeartbeatEvent) {
        discoverIfNeeded(event.value)
    }

    private fun discoverIfNeeded(value: Any) {
        if (monitor.update(value)) {
            // 等待几秒，健康检查通过后才能查到新的实例
            sleep(10)
            discover()
        }
    }

    private fun discover() {
        logger.debug("Discovering new instances from DiscoveryClient")
        val groups = workerManager.listGroupByDiscoverType(DiscoveryTypeEnum.CLOUD)
        groups.forEach { group ->
            group.registryList = discoveryClient.getInstances(group.name).map { convertAddress(it) }
            logger.debug("update group[${group.name}], set registry list: ${group.registryList}")
            workerManager.updateGroup(group)
        }
    }

    private fun convertAddress(instance: ServiceInstance): String {
        return instance.uri.toString()
    }


    companion object {
        private val logger = LoggerFactory.getLogger(WorkerDiscoveryListener::class.java)
    }
}
