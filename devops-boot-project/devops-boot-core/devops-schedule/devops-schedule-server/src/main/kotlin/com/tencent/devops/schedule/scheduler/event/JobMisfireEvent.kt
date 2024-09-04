package com.tencent.devops.schedule.scheduler.event

/**
 * 任务调度错过事件
 * */
class JobMisfireEvent(jobId: String) : JobEvent(jobId)
