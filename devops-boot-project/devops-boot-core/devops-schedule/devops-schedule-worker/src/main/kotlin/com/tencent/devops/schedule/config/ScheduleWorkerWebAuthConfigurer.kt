package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.RpcAuthWebInterceptor
import com.tencent.devops.schedule.constants.WORKER_RPC_V1
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Schedule server api 配置
 */
class ScheduleWorkerWebAuthConfigurer(
    scheduleWorkerProperties: ScheduleWorkerProperties
) : WebMvcConfigurer {

    private val serverProperties = scheduleWorkerProperties.server

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RpcAuthWebInterceptor(serverProperties.accessToken))
            .addPathPatterns("$WORKER_RPC_V1/**")
        super.addInterceptors(registry)
    }
}
