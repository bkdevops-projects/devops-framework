package com.tencent.devops.schedule.mongo.model

import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.ALARM_STATUS_IDX
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.ALARM_STATUS_IDX_DEF
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.EXECUTION_CODE_IDX
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.EXECUTION_CODE_IDX_DEF
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.JOB_ID_IDX
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.JOB_ID_IDX_DEF
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.TRIGGER_CODE_IDX
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.TRIGGER_CODE_IDX_DEF
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.TRIGGER_TIME_IDX
import com.tencent.devops.schedule.mongo.model.TJobLog.Companion.TRIGGER_TIME_IDX_DEF
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * 任务信息模型类
 */
@Document("job_log")
@CompoundIndexes(
    CompoundIndex(name = JOB_ID_IDX, def = JOB_ID_IDX_DEF, background = true),
    CompoundIndex(name = TRIGGER_TIME_IDX, def = TRIGGER_TIME_IDX_DEF, background = true),
    CompoundIndex(name = TRIGGER_CODE_IDX, def = TRIGGER_CODE_IDX_DEF, background = true),
    CompoundIndex(name = EXECUTION_CODE_IDX, def = EXECUTION_CODE_IDX_DEF, background = true),
    CompoundIndex(name = ALARM_STATUS_IDX, def = ALARM_STATUS_IDX_DEF, background = true),
)
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
    var triggerCode: Int,
    var triggerMsg: String? = null,
    var triggerType: Int,
    var scheduledFireTime: LocalDateTime,

    /**
     * execution信息
     */
    var executionTime: LocalDateTime? = null,
    var executionCode: Int,
    var executionMsg: String? = null,

    /**
     * alarm信息
     */
    var alarmStatus: Int,
) {
    companion object {
        const val JOB_ID_IDX = "jobId_idx"
        const val JOB_ID_IDX_DEF = "{'jobId': 1 , 'triggerTime': 1}"
        const val TRIGGER_TIME_IDX = "triggerTime_idx"
        const val TRIGGER_TIME_IDX_DEF = "{'triggerTime': 1}"
        const val TRIGGER_CODE_IDX = "triggerCode_idx"
        const val TRIGGER_CODE_IDX_DEF = "{'triggerCode': 1}"
        const val EXECUTION_CODE_IDX = "executionCode_idx"
        const val EXECUTION_CODE_IDX_DEF = "{'executionCode': 1}"
        const val ALARM_STATUS_IDX = "alarmStatus_idx"
        const val ALARM_STATUS_IDX_DEF = "{'alarmStatus': 1}"
    }
}
