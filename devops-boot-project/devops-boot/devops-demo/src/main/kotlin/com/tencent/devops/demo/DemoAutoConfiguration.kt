package com.tencent.devops.demo

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 用于框架演示自动配置
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(DemoProperties::class)
class DemoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GreetingService::class)
    fun demoGreetingService(demoProperties: DemoProperties): GreetingService {
        return DemoGreetingService(demoProperties)
    }
}