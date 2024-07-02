package com.tencent.devops.plugin.core

import com.tencent.devops.plugin.api.EXTENSION_LOCATION
import com.tencent.devops.plugin.api.ExtensionType
import com.tencent.devops.plugin.api.PluginInfo
import com.tencent.devops.plugin.api.PluginMetadata
import org.springframework.boot.loader.LaunchedURLClassLoader
import org.springframework.boot.loader.archive.Archive
import org.springframework.boot.loader.archive.JarFileArchive
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.LinkedList
import java.util.Properties
import java.util.jar.JarFile

/**
 * 插件加载器
 */
class PluginLoader(
    private val pluginPath: Path,
) {

    val classLoader = createClassloader(pluginPath)

    init {
        check(Files.exists(pluginPath)) { "Plugin file[$pluginPath] does not exist." }
    }

    fun loadPlugin(): PluginInfo {
        JarFile(pluginPath.toFile()).use {
            val digest = calculateDigest()
            val metadata = resolveMetadata(it)
            val extensions = resolveExtensions(it)
            return PluginInfo(
                id = metadata.id,
                metadata = metadata,
                digest = digest,
                extensionPoints = extensions[ExtensionType.POINT].orEmpty(),
                extensionControllers = extensions[ExtensionType.CONTROLLER].orEmpty(),
            )
        }
    }

    private fun resolveExtensions(jarFile: JarFile): HashMap<ExtensionType, LinkedList<String>> {
        try {
            val result = HashMap<ExtensionType, LinkedList<String>>()
            val properties = Properties()
            val jarEntry = jarFile.getJarEntry(EXTENSION_LOCATION)
            check(jarEntry != null) { "[$EXTENSION_LOCATION] does not exist in plugin [$pluginPath]" }
            jarFile.getInputStream(jarEntry).use {
                properties.load(it)
            }
            ExtensionType.values().forEach { type ->
                val list = result.getOrPut(type) { LinkedList() }
                properties.getProperty(type.identifier).orEmpty()
                    .split(",")
                    .filter { it.isNotBlank() }
                    .forEach { list.add(it.trim()) }
            }
            return result
        } catch (ex: IOException) {
            throw IllegalArgumentException("Unable to load extensions from location [$EXTENSION_LOCATION]", ex)
        }
    }

    private fun resolveMetadata(jarFile: JarFile): PluginMetadata {
        try {
            val manifest = jarFile.manifest
            check(manifest != null) { "[$MANIFEST_LOCATION] does not exist in plugin [$pluginPath]" }
            val attributes = manifest.mainAttributes
            val id = attributes.getValue(PLUGIN_ID).orEmpty().trim()
            check(id.isNotEmpty()) { "Required manifest attribute $PLUGIN_ID is null" }
            val version = attributes.getValue(PLUGIN_VERSION).orEmpty().trim()
            val scope = resolveScope(attributes.getValue(PLUGIN_SCOPE).orEmpty().trim())
            val author = attributes.getValue(PLUGIN_AUTHOR).orEmpty().trim()
            val description = attributes.getValue(PLUGIN_DESCRIPTION).orEmpty().trim()
            return PluginMetadata(
                id = id,
                name = id,
                version = version,
                scope = scope,
                author = author,
                description = description,
            )
        } catch (ex: IOException) {
            throw IllegalArgumentException("Unable to load manifest from location [$MANIFEST_LOCATION]", ex)
        }
    }

    private fun resolveScope(value: String): List<String> {
        if (value.isEmpty() || value == "*") {
            return emptyList()
        }
        val scope = value.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
        return if (scope.contains("*")) emptyList() else scope
    }

    private fun calculateDigest(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        pluginPath.toFile().inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var sizeRead = input.read(buffer)
            while (sizeRead != -1) {
                digest.update(buffer, 0, sizeRead)
                sizeRead = input.read(buffer)
            }
            return digest.digest().fold("") { str, it -> str + "%02x".format(it) }
        }
    }

    private fun createClassloader(pluginPath: Path): ClassLoader {
        val jarArchive = JarFileArchive(pluginPath.toFile())
        val archives = jarArchive.getNestedArchives(searchFilter, nestedFilter)
        val urls = mutableListOf<URL>(jarArchive.url)
        archives.forEach {
            urls.add(it.url)
        }
        return LaunchedURLClassLoader(false, jarArchive, urls.toTypedArray(), javaClass.classLoader)
    }

    companion object {
        private const val MANIFEST_LOCATION = "META-INF/MANIFEST.MF"
        private const val PLUGIN_ID = "Plugin-Id"
        private const val PLUGIN_VERSION = "Plugin-Version"
        private const val PLUGIN_SCOPE = "Plugin-Scope"
        private const val PLUGIN_AUTHOR = "Plugin-Author"
        private const val PLUGIN_DESCRIPTION = "Plugin-Description"
        val searchFilter = Archive.EntryFilter { entry -> entry.name.startsWith("lib/") }
        val nestedFilter = Archive.EntryFilter { entry ->
            !entry.isDirectory && entry.name.startsWith("lib/")
        }
    }
}
