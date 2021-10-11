package com.tencent.devops.sample.extension

import com.tencent.devops.plugin.api.ExtensionPoint

/**
 * 定义扩展点
 */
interface PrintExtension : ExtensionPoint {

    /**
     * print content
     * 可以通过插件自定义实现打印逻辑，如System IO、Logger、Printer等
     */
    fun print(content: String)
}
