package com.tencent.devops.plugin.core

import java.net.URLClassLoader
import java.nio.file.Path

/**
 * 插件classloader
 * 每个插件对应一个单独的classloader
 */
class PluginClassLoader(
    pluginPath: Path,
    parentLoader: ClassLoader
) : URLClassLoader(arrayOf(pluginPath.toUri().toURL()), parentLoader) {
    override fun loadClass(name: String?): Class<*> {
        return super.loadClass(name)
    }

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }

    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        return super.loadClass(name, resolve)
    }
}
