package com.tencent.devops.plugin.core

import com.tencent.devops.plugin.api.ExtensionPoint
import com.tencent.devops.plugin.api.ExtensionRegistry
import com.tencent.devops.plugin.api.PluginInfo
import com.tencent.devops.plugin.api.PluginManager
import com.tencent.devops.plugin.api.PluginScanner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener

/**
 * 插件管理器
 */
class DefaultPluginManager(
    private val pluginScanner: PluginScanner,
    private val extensionRegistry: ExtensionRegistry
) : PluginManager {

    private val pluginMap = mutableMapOf<String, PluginInfo>()

    @Value("\${spring.application.name}")
    private var applicationName: String? = null

    @EventListener(ApplicationReadyEvent::class)
    @Synchronized
    override fun load() {
        try {
            pluginScanner.scan().forEach {
                val pluginLoader = PluginLoader(it)
                val pluginInfo = pluginLoader.loadPlugin()
                registerPluginIfNecessary(pluginInfo, pluginLoader.classLoader)
            }
        } catch (ignored: Exception) {
            logger.error("Failed to load plugin: ${ignored.message}", ignored)
            throw ignored
        }
    }

    @Synchronized
    override fun load(id: String) {
        try {
            val path = pluginScanner.scan(id)
            checkNotNull(path) { "Plugin[$id] jar file not found" }
            val pluginLoader = PluginLoader(path)
            val pluginInfo = pluginLoader.loadPlugin()
            registerPluginIfNecessary(pluginInfo, pluginLoader.classLoader)
        } catch (ignored: Exception) {
            logger.error("Failed to load plugin[$id]: ${ignored.message}", ignored)
            throw ignored
        }
    }

    @Synchronized
    override fun unload(id: String) {
        try {
            if (!pluginMap.containsKey(id)) {
                return
            }
            extensionRegistry.unregisterExtensionPointsByPlugin(id)
            extensionRegistry.unregisterExtensionControllerByPlugin(id)
            pluginMap.remove(id)
            logger.info("Success unregister plugin[$id]")
        } catch (ignored: Exception) {
            logger.error("Failed to unload plugin[$id]: ${ignored.message}", ignored)
            throw ignored
        }
    }

    override fun <T : ExtensionPoint> findExtensionPoints(clazz: Class<T>): List<T> {
        return extensionRegistry.findExtensionPoints(clazz)
    }

    override fun getPluginMap(): Map<String, PluginInfo> {
        return pluginMap
    }

    /**
     * 注销插件[pluginInfo]
     * 如果插件scope不符合，或者已经存在则不会注册
     */
    private fun registerPluginIfNecessary(pluginInfo: PluginInfo, classLoader: ClassLoader) {
        if (!checkScope(pluginInfo)) {
            logger.info("Plugin[${pluginInfo.id}] scope does not contain $applicationName, skip register")
            return
        }
        if (checkExist(pluginInfo)) {
            logger.info("Plugin[${pluginInfo.id}] has been loaded, skip register")
            return
        }
        if (pluginMap.containsKey(pluginInfo.id)) {
            // unregister loaded extension points
            extensionRegistry.unregisterExtensionPointsByPlugin(pluginInfo.id)
            // unregister loaded extension controller
            extensionRegistry.unregisterExtensionControllerByPlugin(pluginInfo.id)
            // remove
            pluginMap.remove(pluginInfo.id)
        }

        // register extension points
        pluginInfo.extensionPoints.forEach {
            val type = classLoader.loadClass(it)
            val name = type.interfaces[0].name
            extensionRegistry.registerExtensionPoint(pluginInfo.id, name, type)
        }
        // register extension controller
        pluginInfo.extensionControllers.forEach {
            val type = classLoader.loadClass(it)
            extensionRegistry.registerExtensionController(pluginInfo.id, it, type)
        }
        // save
        pluginMap[pluginInfo.id] = pluginInfo
        logger.info("Success register plugin[${pluginInfo.id}]")
    }

    /**
     * 检查插件范围是否有效
     */
    private fun checkScope(pluginInfo: PluginInfo): Boolean {
        if (pluginInfo.metadata.scope.isEmpty()) {
            return true
        }
        return pluginInfo.metadata.scope.contains(applicationName)
    }

    /**
     * 检查插件是否已存在
     */
    private fun checkExist(pluginInfo: PluginInfo): Boolean {
        return pluginMap[pluginInfo.id]?.digest == pluginInfo.digest
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultPluginManager::class.java)
    }
}
