package com.tencent.devops.schedule.pojo.log

import com.fasterxml.jackson.annotation.JsonFormat
import com.tencent.devops.schedule.constants.DATE_TIME_PATTERN
import com.tencent.devops.schedule.enums.AlarmStatusEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import java.time.LocalDateTime

data class JobLog(
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
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    var triggerTime: LocalDateTime,
    var triggerCode: Int = ExecutionCodeEnum.INITIALED.code(),
    var triggerMsg: String? = null,
    var triggerType: Int,

    /**
     * handle信息
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    var executionTime: LocalDateTime? = null,
    var executionCode: Int = ExecutionCodeEnum.INITIALED.code(),
    var executionMsg: String? = null,

    /**
     * alarm信息
     */
    var alarmStatus: Int = AlarmStatusEnum.TODO.code(),
)
