package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.constants.WorkerRegistryMode
import com.tencent.devops.schedule.executor.DefaultJobExecutor
import com.tencent.devops.schedule.executor.JobExecutor
import com.tencent.devops.schedule.hearbeat.DefaultHeartbeat
import com.tencent.devops.schedule.hearbeat.Heartbeat
import com.tencent.devops.schedule.hearbeat.NoOpHeartbeat
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ScheduleWorkerProperties::class)
@Import(
    ScheduleWorkerWebConfiguration::class
)
class ScheduleWorkerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun heartbeat(
        scheduleWorkerProperties: ScheduleWorkerProperties,
        serverRpcClient: ServerRpcClient
    ): Heartbeat {
        return when (scheduleWorkerProperties.mode) {
            WorkerRegistryMode.AUTO -> DefaultHeartbeat(scheduleWorkerProperties, serverRpcClient)
            else -> NoOpHeartbeat()
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun jobExecutor(
        scheduleWorkerProperties: ScheduleWorkerProperties,
        serverRpcClient: ServerRpcClient
    ): JobExecutor {
        return DefaultJobExecutor(scheduleWorkerProperties, serverRpcClient)
    }
}
