package com.tencent.devops.logging.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("devops.logging")
data class DevopsLoggingProperties(
    /**
     * 日志文件所在路径
     */
    var path: String? = null,

    /**
     * 正常应用日志文件名
     */
    var appFile: String? = null,

    /**
     * 错误应用日志文件名
     */
    var errorFile: String? = null,

    /**
     * access日志文件名
     */
    var accessFile: String? = null,

    /**
     * 日志格式
     */
    var filePattern: String? = null
)
