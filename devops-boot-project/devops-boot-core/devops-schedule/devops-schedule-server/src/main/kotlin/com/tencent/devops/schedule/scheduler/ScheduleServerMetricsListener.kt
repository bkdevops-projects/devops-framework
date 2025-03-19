package com.tencent.devops.schedule.scheduler

import com.tencent.devops.schedule.config.ScheduleServerProperties
import com.tencent.devops.schedule.scheduler.event.JobMisfireEvent
import com.tencent.devops.schedule.scheduler.event.JobTriggerEvent
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import java.time.Duration

/**
 * 调度服务器监控指标监听器
 * */
class ScheduleServerMetricsListener(
    val registry: MeterRegistry,
    scheduleServerProperties: ScheduleServerProperties,
) {
    private val slo = scheduleServerProperties.slo.map { Duration.ofSeconds(it.toLong()) }.toTypedArray()

    @EventListener(JobMisfireEvent::class)
    fun jobMisfire(event: JobMisfireEvent) {
        with(event) {
            // 任务超时未触发
            logger.info("Job[$jobId] is misfire")
            getJobCount(CountType.MISFIRE).increment()
        }
    }

    @EventListener(JobTriggerEvent::class)
    fun jobTrigger(event: JobTriggerEvent) {
        with(event) {
            logger.info("Trigger job[$jobId], elapsed ${event.duration.toMillis()} ms,job latency ${event.latency.toMillis()} ms.")
            getTriggerLatencyTimer().record(event.latency)
            getTriggerTimer().record(event.duration)
            getJobCount(CountType.TRIGGER).increment()
        }
    }

    private fun getTriggerLatencyTimer(): Timer {
        return Timer.builder("devops.schedule.server.trigger.latency")
            .description("任务调度延迟")
            .publishPercentileHistogram()
            .serviceLevelObjectives(*slo)
            .minimumExpectedValue(Duration.ofSeconds(1))
            .maximumExpectedValue(Duration.ofSeconds(10))
            .register(registry)
    }

    private fun getTriggerTimer(): Timer {
        return Timer.builder("devops.schedule.server.trigger.time")
            .description("任务调度耗时")
            .register(registry)
    }

    private fun getJobCount(type: CountType): Counter {
        return Counter.builder("devops.schedule.server.trigger.count")
            .description("任务调度数量")
            .tag("type", type.name)
            .register(registry)
    }

    enum class CountType {
        MISFIRE,
        TRIGGER,
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ScheduleServerMetricsListener::class.java)
    }
}
