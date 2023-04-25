package com.tencent.devops.plugin.spring

import com.tencent.devops.plugin.api.PluginManager
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["org.springframework.boot.actuate.endpoint.annotation.Endpoint"])
@AutoConfigureAfter(EndpointAutoConfiguration::class)
@ConditionalOnProperty(prefix = "management.endpoint.devopsPlugin", name = ["enabled"], havingValue = "true")
class PluginEndpointAutoConfiguration {

    @Bean
    fun pluginEndpoint(pluginManager: PluginManager) = PluginEndpoint(pluginManager)
}
