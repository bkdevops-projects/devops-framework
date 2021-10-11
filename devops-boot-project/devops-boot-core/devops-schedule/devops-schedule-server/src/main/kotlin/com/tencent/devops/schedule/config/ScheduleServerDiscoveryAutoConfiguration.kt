package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.discovery.WorkerDiscoveryListener
import com.tencent.devops.schedule.manager.WorkerManager
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * spring cloud坏境下自动配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(DiscoveryClient::class)
@AutoConfigureAfter(
    name = [
        "org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration"
    ]
)
class ScheduleServerDiscoveryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun workerDiscoveryListener(
        discoveryClient: DiscoveryClient,
        workerManager: WorkerManager
    ): WorkerDiscoveryListener {
        return WorkerDiscoveryListener(discoveryClient, workerManager)
    }
}
