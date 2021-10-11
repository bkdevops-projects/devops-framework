package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.RpcAuthWebInterceptor
import com.tencent.devops.schedule.constants.SERVER_API_V1
import com.tencent.devops.schedule.constants.SERVER_RPC_V1
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Schedule server api 配置
 */
class ScheduleServerWebAuthConfigurer(
    scheduleServerProperties: ScheduleServerProperties
) : WebMvcConfigurer {

    private val contextPath = scheduleServerProperties.contextPath.trimEnd('/')
    private val authProperties = scheduleServerProperties.auth

    /**
     * 跨域访问配置
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("$contextPath/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        // rpc
        registry.addInterceptor(RpcAuthWebInterceptor(authProperties.accessToken))
            .addPathPatterns("$contextPath/$SERVER_RPC_V1/**")
        // api
        registry.addInterceptor(ScheduleServerAuthInterceptor(authProperties))
            .addPathPatterns("$contextPath/$SERVER_API_V1/**")
            .excludePathPatterns(
                "$contextPath/$SERVER_API_V1/",
                "$contextPath/$SERVER_API_V1/user/info",
                "$contextPath/$SERVER_API_V1/user/login",
                "$contextPath/$SERVER_API_V1/user/logout",
                "$contextPath/$SERVER_API_V1/dict/**",
            )
        super.addInterceptors(registry)
    }
}
