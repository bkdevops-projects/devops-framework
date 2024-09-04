package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.api.WorkerRpcClient
import com.tencent.devops.schedule.manager.DefaultJobManager
import com.tencent.devops.schedule.manager.DefaultWorkerManager
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.manager.WorkerManager
import com.tencent.devops.schedule.provider.JobProvider
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.provider.WorkerProvider
import com.tencent.devops.schedule.scheduler.DefaultJobScheduler
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.scheduler.ScheduleServerMetricsListener
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ScheduleServerProperties::class)
@Import(
    ScheduleServerWebConfiguration::class,
    ScheduleServerMetricsListener::class,
)
class ScheduleServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun jobScheduler(
        jobManager: JobManager,
        workerManager: WorkerManager,
        lockProvider: LockProvider,
        scheduleServerProperties: ScheduleServerProperties,
        workerRpcClient: WorkerRpcClient,
        registry: MeterRegistry,
        publisher: ApplicationEventPublisher,
    ): JobScheduler {
        return DefaultJobScheduler(
            jobManager,
            workerManager,
            lockProvider,
            scheduleServerProperties,
            workerRpcClient,
            registry,
            publisher,
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun jobManager(jobProvider: JobProvider, workerProvider: WorkerProvider): JobManager {
        return DefaultJobManager(jobProvider, workerProvider)
    }

    @Bean
    @ConditionalOnMissingBean
    fun workerManager(workerProvider: WorkerProvider): WorkerManager {
        return DefaultWorkerManager(workerProvider)
    }
}
