package com.tencent.devops.plugin.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("plugin")
data class PluginProperties(
    /**
     * 插件路径
     */
    var path: String = "plugins"
)
