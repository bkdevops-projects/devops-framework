package com.tencent.devops.plugin.api

/**
 * 扩展类型
 */
enum class ExtensionType(val identifier: String) {
    /**
     * 扩展controller，可以通过插件实现新增接口
     */
    CONTROLLER("ExtensionController"),

    /**
     * 扩展点，可以通过插件实现新增逻辑
     */
    POINT("ExtensionPoint")
}
