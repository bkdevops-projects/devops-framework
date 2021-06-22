package com.tencent.devops.plugin.api

import java.nio.file.Path

/**
 * 插件扫描器
 */
interface PluginScanner {

    /**
     * 扫描插件路径
     * @return 插件路径集合
     */
    fun scan(): List<Path>

    /**
     * 扫描指定插件路径
     * 此方法要求插件必须按照指定格式命名：plugin-xxx-1.0.0.jar，其中xxx代表插件id
     * @param id 插件id
     */
    fun scan(id: String): Path?
}
