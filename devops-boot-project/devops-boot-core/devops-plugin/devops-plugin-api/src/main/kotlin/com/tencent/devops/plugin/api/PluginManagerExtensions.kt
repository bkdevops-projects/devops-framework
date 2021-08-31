package com.tencent.devops.plugin.api

/**
 * 查找扩展点, kotlin风格扩展函数
 */
inline fun <reified T : ExtensionPoint> PluginManager.find(): List<T> {
    return this.findExtensionPoints(T::class.java)
}

/**
 * 查找扩展点并遍历执行扩展逻辑
 */
inline fun <reified T : ExtensionPoint> PluginManager.applyExtension(block: T.() -> Unit) {
    this.findExtensionPoints(T::class.java).forEach {
        block(it)
    }
}
