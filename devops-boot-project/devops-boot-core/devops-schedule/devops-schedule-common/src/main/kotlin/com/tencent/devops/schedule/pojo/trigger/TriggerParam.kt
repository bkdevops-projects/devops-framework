package com.tencent.devops.schedule.pojo.trigger

import com.fasterxml.jackson.annotation.JsonFormat
import com.tencent.devops.schedule.constants.DATE_TIME_PATTERN
import java.time.LocalDateTime

data class TriggerParam(
    var jobId: String,
    var jobMode: Int,
    var source: String? = null,
    var image: String? = null,
    var jobHandler: String,
    var jobParam: String,
    var blockStrategy: Int,
    var jobTimeout: Int = -1,
    var logId: String,
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    var triggerTime: LocalDateTime,
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    var scheduledFireTime: LocalDateTime,
    var broadcastIndex: Int = 0,
    var broadcastTotal: Int = 0,
    var workerAddress: String? = null,
    var updateTime: LocalDateTime,
    var command: List<String>? = null,
    var cmdFileName: String? = null,
)
