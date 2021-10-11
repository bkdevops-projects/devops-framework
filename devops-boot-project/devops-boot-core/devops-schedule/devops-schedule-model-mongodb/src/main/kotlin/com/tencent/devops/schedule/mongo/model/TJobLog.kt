package com.tencent.devops.schedule.mongo.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * 任务信息模型类
 */
@Document("job_log")
data class TJobLog(

    var id: String? = null,

    /**
     * 任务id
     */
    var jobId: String,

    /**
     * 任务工作组
     */
    var groupId: String,

    /**
     * job 参数
     */
    var jobHandler: String? = null,
    var jobParam: String? = null,

    /**
     * worker参数
     */
    var workerAddress: String? = null,
    var workerShardingParam: String? = null,
    var workerRetryCount: Int? = null,

    /**
     * trigger信息
     */
    var triggerTime: LocalDateTime,
    var triggerCode : Int,
    var triggerMsg: String? = null,
    var triggerType: Int,

    /**
     * execution信息
     */
    var executionTime: LocalDateTime? = null,
    var executionCode: Int,
    var executionMsg: String? = null,

    /**
     * alarm信息
     */
    var alarmStatus: Int? = null
)
