package com.tencent.devops.schedule.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ComponentScan("com.tencent.devops.schedule.web")
class ScheduleServerWebConfiguration {

    @Bean
    @ConditionalOnProperty(
        prefix = ScheduleServerProperties.PREFIX,
        name = ["ui.enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    fun scheduleServerUiConfigurer(
        scheduleServerProperties: ScheduleServerProperties
    ): ScheduleServerWebUiConfigurer {
        return ScheduleServerWebUiConfigurer(scheduleServerProperties)
    }

    @Bean
    fun scheduleServerAuthConfigurer(
        scheduleServerProperties: ScheduleServerProperties
    ): ScheduleServerWebAuthConfigurer {
        return ScheduleServerWebAuthConfigurer(scheduleServerProperties)
    }
}
