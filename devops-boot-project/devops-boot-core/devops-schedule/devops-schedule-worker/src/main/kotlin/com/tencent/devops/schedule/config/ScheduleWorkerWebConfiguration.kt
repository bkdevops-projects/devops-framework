package com.tencent.devops.schedule.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ComponentScan("com.tencent.devops.schedule.web")
class ScheduleWorkerWebConfiguration {

    @Bean
    fun scheduleServerAuthConfigurer(
        scheduleWorkerProperties: ScheduleWorkerProperties
    ): ScheduleWorkerWebAuthConfigurer {
        return ScheduleWorkerWebAuthConfigurer(scheduleWorkerProperties)
    }
}
