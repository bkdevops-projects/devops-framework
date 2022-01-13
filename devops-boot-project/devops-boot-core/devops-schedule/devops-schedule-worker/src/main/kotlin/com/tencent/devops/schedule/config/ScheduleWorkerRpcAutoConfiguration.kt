package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.RpcAuthRequestInterceptor
import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.remote.RemoteServerRpcClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration(proxyBeanMethods = false)
class ScheduleWorkerRpcAutoConfiguration {

    @Bean(SERVER_REST_TEMPLATE)
    @ConditionalOnMissingBean(name = [SERVER_REST_TEMPLATE])
    fun serverRestTemplate(
        builder: RestTemplateBuilder,
        scheduleWorkerProperties: ScheduleWorkerProperties
    ): RestTemplate {
        val accessToken = scheduleWorkerProperties.server.accessToken
        val restTemplate = builder.build()
        val interceptor = RpcAuthRequestInterceptor(accessToken)
        restTemplate.interceptors.add(interceptor)
        return restTemplate
    }

    @Bean
    @ConditionalOnMissingBean
    fun workerRpcClient(
        @Qualifier(SERVER_REST_TEMPLATE)
        serverRestTemplate: RestTemplate,
        scheduleWorkerProperties: ScheduleWorkerProperties
    ): ServerRpcClient {
        return RemoteServerRpcClient(scheduleWorkerProperties, serverRestTemplate)
    }

    companion object {
        const val SERVER_REST_TEMPLATE = "serverRestTemplate"
    }
}
