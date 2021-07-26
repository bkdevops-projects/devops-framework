package com.tencent.devops.plugin.core

import com.tencent.devops.plugin.api.PluginScanner
import com.tencent.devops.plugin.config.PluginProperties
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

/**
 * 插件扫描器默认实现
 * 扫描指定路径下jar文件
 */
class DefaultPluginScanner(
    private val pluginProperties: PluginProperties
) : PluginScanner {

    override fun scan(): List<Path> {
        val pluginDir = Paths.get(pluginProperties.path)
        if (!Files.isDirectory(pluginDir)) {
            logger.error("Failed to load plugin, Path[$pluginDir] is not a directory.")
            return emptyList()
        }
        return Files.walk(pluginDir).filter { it.toString().endsWith(".jar") }.toList()
    }

    override fun scan(id: String): Path? {
        val pluginDir = Paths.get(pluginProperties.path)
        if (!Files.isDirectory(pluginDir)) {
            logger.error("Failed to load plugin, Path[$pluginDir] is not a directory.")
            return null
        }
        return Files.walk(pluginDir).filter {
            val filename = it.toString()
            val name = filename.substringAfter("-").substringBeforeLast("-")
            name == id
        }.toList().firstOrNull()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultPluginScanner::class.java)
    }
}
