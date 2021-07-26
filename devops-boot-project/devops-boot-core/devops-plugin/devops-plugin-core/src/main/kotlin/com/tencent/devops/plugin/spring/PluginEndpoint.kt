package com.tencent.devops.plugin.spring

import com.tencent.devops.plugin.api.PluginInfo
import com.tencent.devops.plugin.api.PluginManager
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.annotation.Selector
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR

/**
 * 插件相关endpoint
 * 访问地址 {host}/actuator/plugin
 */
@Endpoint(id = "plugin")
class PluginEndpoint(
    private val pluginManager: PluginManager
) {

    /**
     * 读取所有已加载的插件列表
     */
    @ReadOperation
    fun list(): Map<String, PluginInfo> {
        return pluginManager.getPluginMap()
    }

    /**
     * 重新加载所有插件
     */
    @WriteOperation
    fun load(): WebEndpointResponse<String> {
        return try {
            pluginManager.load()
            WebEndpointResponse("ok")
        } catch (ignored: Exception) {
            WebEndpointResponse(ignored.message.orEmpty(), STATUS_INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * 加载指定插件
     * @param id 需要加载的插件id
     */
    @WriteOperation
    fun load(@Selector id: String): WebEndpointResponse<String> {
        return try {
            pluginManager.load(id)
            WebEndpointResponse("ok")
        } catch (ignored: Exception) {
            WebEndpointResponse(ignored.message.orEmpty(), STATUS_INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * 卸载指定插件
     * @param id 需要卸载的插件id
     */
    @DeleteOperation
    fun unload(@Selector id: String): WebEndpointResponse<String> {
        return try {
            pluginManager.unload(id)
            WebEndpointResponse("ok")
        } catch (ignored: Exception) {
            WebEndpointResponse(ignored.message.orEmpty(), STATUS_INTERNAL_SERVER_ERROR)
        }
    }
}
