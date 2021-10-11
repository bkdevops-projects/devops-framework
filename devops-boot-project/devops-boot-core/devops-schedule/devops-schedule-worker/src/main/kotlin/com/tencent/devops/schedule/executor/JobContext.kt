package com.tencent.devops.schedule.executor

import java.time.LocalDateTime

data class JobContext(
    /**
     * 任务id
     */
    var jobId: String,
    /**
     * 任务参数
     */
    var jobParamMap: Map<String, Any>,
    /**
     * 本次任务执行日志id
     */
    var logId: String,
    /**
     * 本次任务触发时间
     */
    var triggerTime: LocalDateTime,
    /**
     * 分片广播序号
     */
    var broadcastIndex: Int = 0,
    /**
     * 分片广播总数
     */
    var broadcastTotal: Int = 1
)
