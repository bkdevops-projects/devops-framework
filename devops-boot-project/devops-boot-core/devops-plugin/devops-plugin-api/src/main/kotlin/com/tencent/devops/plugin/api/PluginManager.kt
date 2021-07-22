package com.tencent.devops.plugin.api

/**
 * 插件管理器
 */
interface PluginManager {

    /**
     * 加载所有插件
     */
    fun load()

    /**
     * 加载插件
     * @param id 插件id
     */
    fun load(id: String)

    /**
     * 卸载插件
     * @param id 插件id
     */
    fun unload(id: String)

    /**
     * 查找扩展点列表
     * @param clazz 扩展点类型
     */
    fun <T : ExtensionPoint> findExtensionPoints(clazz: Class<T>): List<T>

    /**
     * 获取注册的插件列表
     */
    fun getPluginMap(): Map<String, PluginInfo>
}
