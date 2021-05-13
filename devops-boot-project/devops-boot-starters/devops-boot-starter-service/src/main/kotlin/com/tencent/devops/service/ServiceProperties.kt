package com.tencent.devops.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("service")
data class ServiceProperties(
    /**
     * 服务名称前缀
     */
    var prefix: String = "",

    /**
     * 服务名称后缀
     */
    var suffix: String = "",

    /**
     * 服务名称
     */
    var name: String = "application"
) {
    /**
     * 返回应用全称，和`spring.application.name`保持一致
     */
    fun getApplicationName(): String {
        return "$prefix$name$suffix"
    }
}
