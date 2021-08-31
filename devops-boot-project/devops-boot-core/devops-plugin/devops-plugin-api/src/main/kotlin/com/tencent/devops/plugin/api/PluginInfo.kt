package com.tencent.devops.plugin.api

/**
 * 插件信息
 */
class PluginInfo(
    /**
     * 插件id
     */
    val id: String,
    /**
     * 插件元数据信息
     */
    val metadata: PluginMetadata,
    /**
     * 文件摘要
     */
    val digest: String,
    /**
     * 插件扩展点
     */
    val extensionPoints: List<String>,
    /**
     * 扩展controller
     */
    val extensionControllers: List<String>
)
