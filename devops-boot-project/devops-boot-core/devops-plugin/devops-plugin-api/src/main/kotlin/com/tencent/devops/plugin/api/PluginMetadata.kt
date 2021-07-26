package com.tencent.devops.plugin.api

/**
 * 插件metadata信息
 */
data class PluginMetadata(
    /**
     * 插件id，要求唯一
     */
    val id: String,
    /**
     * 插件名称，要求唯一，先保持和id一致
     */
    val name: String,
    /**
     * 插件版本，语义化版本格式
     */
    val version: String,
    /**
     * 插件生效范围
     */
    val scope: List<String>,
    /**
     * 插件作者
     */
    val author: String? = null,
    /**
     * 插件描述
     */
    val description: String? = null
)
