package com.tencent.devops.plugin

import com.tencent.devops.plugin.api.ExtensionRegistry
import com.tencent.devops.plugin.api.PluginManager
import com.tencent.devops.plugin.api.PluginScanner
import com.tencent.devops.plugin.config.PluginProperties
import com.tencent.devops.plugin.core.DefaultPluginManager
import com.tencent.devops.plugin.core.DefaultPluginScanner
import com.tencent.devops.plugin.spring.SpringExtensionRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PluginProperties::class)
class PluginAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PluginScanner::class)
    fun pluginScanner(pluginProperties: PluginProperties) = DefaultPluginScanner(pluginProperties)

    @Bean
    @ConditionalOnMissingBean(ExtensionRegistry::class)
    fun extensionRegistry() = SpringExtensionRegistry()

    @Bean
    @ConditionalOnMissingBean(PluginManager::class)
    fun pluginManager(
        pluginScanner: PluginScanner,
        extensionRegistry: ExtensionRegistry
    ): PluginManager {
        return DefaultPluginManager(pluginScanner, extensionRegistry)
    }
}
