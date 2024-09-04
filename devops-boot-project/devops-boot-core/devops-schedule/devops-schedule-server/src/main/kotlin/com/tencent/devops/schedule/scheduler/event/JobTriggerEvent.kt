package com.tencent.devops.schedule.scheduler.event

import java.time.Duration

/**
 * 任务触发事件
 * */
class JobTriggerEvent(
    /**
     * 触发耗时
     * */
    val duration: Duration,
    /**
     * 触发的任务延迟
     * */
    val latency: Duration,
    jobId: String,
) : JobEvent(jobId) {
    companion object {
        fun create(jobId: String, triggerStartTime: Long, triggerEndTime: Long, triggerTime: Long): JobTriggerEvent {
            return JobTriggerEvent(
                Duration.ofMillis(triggerEndTime - triggerStartTime),
                Duration.ofMillis(triggerEndTime - triggerTime),
                jobId,
            )
        }
    }
}
