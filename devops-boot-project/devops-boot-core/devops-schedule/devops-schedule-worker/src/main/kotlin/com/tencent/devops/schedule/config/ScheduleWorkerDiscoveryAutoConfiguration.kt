package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.RpcAuthRequestInterceptor
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(DiscoveryClient::class)
@AutoConfigureBefore(ScheduleWorkerRpcAutoConfiguration::class)
@AutoConfigureAfter(
    name = [
        "org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration"
    ]
)
class ScheduleWorkerDiscoveryAutoConfiguration {

    @Bean(ScheduleWorkerRpcAutoConfiguration.SERVER_REST_TEMPLATE)
    @ConditionalOnMissingBean(name = [ScheduleWorkerRpcAutoConfiguration.SERVER_REST_TEMPLATE])
    @ConditionalOnSingleCandidate(DiscoveryClient::class)
    @ConditionalOnProperty(
        prefix = ScheduleWorkerProperties.PREFIX,
        name = ["mode"],
        havingValue = "DISCOVERY",
        matchIfMissing = false
    )
    @LoadBalanced
    fun loadBalancedServerRestTemplate(
        builder: RestTemplateBuilder,
        scheduleWorkerProperties: ScheduleWorkerProperties
    ): RestTemplate {
        val accessToken = scheduleWorkerProperties.server.accessToken
        val restTemplate = builder.build()
        val interceptor = RpcAuthRequestInterceptor(accessToken)
        restTemplate.interceptors.add(interceptor)
        return restTemplate
    }
}
