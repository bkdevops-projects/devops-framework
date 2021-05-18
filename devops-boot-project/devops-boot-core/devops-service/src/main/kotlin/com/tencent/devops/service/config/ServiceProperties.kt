package com.tencent.devops.service.config

import com.tencent.devops.service.config.ServiceProperties.Companion.PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(PREFIX)
data class ServiceProperties(
    /**
     * 服务名称前缀，如prefix-，默认为空
     *
     * 用于服务注册与获取配置时，添加服务前缀
     */
    var prefix: String = "",

    /**
     * 服务名称后缀, 如-suffix，默认为空
     *
     * 用于服务注册与获取配置时，添加服务前缀
     */
    var suffix: String = "",
) {
    companion object {
        const val PREFIX = "service"
    }
}
