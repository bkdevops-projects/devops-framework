package com.tencent.devops.schedule.thread

import com.tencent.devops.schedule.executor.JobHandler
import com.tencent.devops.schedule.pojo.trigger.TriggerParam

data class TriggerTask(
    val jobId: String,
    val jobHandler: JobHandler,
    val param: TriggerParam,
)
