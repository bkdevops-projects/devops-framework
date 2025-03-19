package com.tencent.devops.schedule.scheduler

import com.tencent.devops.schedule.pojo.job.JobInfo

/**
 * 任务触发上下文
 * */
data class JobTriggerContext(
    /**
     * 任务信息
     * */
    val job: JobInfo,
    /**
     * 触发时间
     * */
    val fireTime: Long,
    /**
     * 上次触发时间
     * */
    val prevFireTime: Long? = null,
    /**
     * 下次触发时间
     * */
    val nextFireTime: Long? = null,
    /**
     * 调度触发时间
     * */
    val scheduledFireTime: Long? = null,
)
