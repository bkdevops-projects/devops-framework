package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.RpcAuthRequestInterceptor
import com.tencent.devops.schedule.api.WorkerRpcClient
import com.tencent.devops.schedule.remote.RemoteWorkerRpcClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration(proxyBeanMethods = false)
class ScheduleServerRpcAutoConfiguration {

    @Bean(WORKER_REST_TEMPLATE)
    @ConditionalOnMissingBean(name = [WORKER_REST_TEMPLATE])
    fun restTemplate(
        builder: RestTemplateBuilder,
        scheduleServerProperties: ScheduleServerProperties
    ): RestTemplate {
        val accessToken = scheduleServerProperties.auth.accessToken
        val restTemplate = builder.build()
        val interceptor = RpcAuthRequestInterceptor(accessToken)
        restTemplate.interceptors.add(interceptor)
        return restTemplate
    }

    @Bean
    @ConditionalOnMissingBean
    fun serverRpcClient(
        scheduleWorkerProperties: ScheduleServerProperties,
        @Qualifier(WORKER_REST_TEMPLATE)
        workerRestTemplate: RestTemplate
    ): WorkerRpcClient {
        return RemoteWorkerRpcClient(workerRestTemplate)
    }

    companion object {
        const val WORKER_REST_TEMPLATE = "workerRestTemplate"
    }
}
