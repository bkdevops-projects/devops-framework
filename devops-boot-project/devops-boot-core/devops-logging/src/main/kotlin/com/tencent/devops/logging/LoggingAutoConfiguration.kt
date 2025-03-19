package com.tencent.devops.logging

import com.tencent.devops.logging.props.DevopsLoggingProperties
import com.tencent.devops.logging.system.DevopsLoggingApplicationListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(DevopsLoggingProperties::class)
class LoggingAutoConfiguration {
    @Bean
    fun devopsLoggingApplicationListener() = DevopsLoggingApplicationListener()
}
