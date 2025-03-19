package com.tencent.devops.schedule.mongo.model

import com.tencent.devops.schedule.mongo.model.TJobInfo.Companion.NEXT_TRIGGER_TIME_IDX
import com.tencent.devops.schedule.mongo.model.TJobInfo.Companion.NEXT_TRIGGER_TIME_IDX_DEF
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * 任务信息模型类
 */
@Document("job_info")
@CompoundIndex(name = NEXT_TRIGGER_TIME_IDX, def = NEXT_TRIGGER_TIME_IDX_DEF, background = true)
data class TJobInfo(

    var id: String? = null,

    /**
     * 任务名称
     */
    var name: String,

    /**
     * 描述
     */
    var description: String? = null,

    /**
     * 所属工作组
     */
    var groupId: String,

    /**
     * 创建时间
     */
    var createTime: LocalDateTime,

    /**
     * 状态更新时间
     */
    var updateTime: LocalDateTime,

    /**
     * 调度类型，参考
     */
    var scheduleType: Int,

    /**
     * 调度配置，其含义取决于调度类型
     */
    var scheduleConf: String,

    /**
     * 过期处理策略
     */
    var misfireStrategy: Int,

    /**
     * 路由策略
     */
    var routeStrategy: Int,

    /**
     * 执行器模式
     */
    var jobMode: Int,

    /**
     * 执行器名称
     */
    var jobHandler: String,

    /**
     * 任务参数
     */
    var jobParam: String,

    /**
     * 任务超时时间
     */
    var jobTimeout: Int,

    /**
     * 阻塞处理策略
     */
    var blockStrategy: Int,

    /**
     * 最大重试次数
     */
    var maxRetryCount: Int,

    /**
     * 任务状态
     */
    var triggerStatus: Int,

    /**
     * 上次执行时间
     */
    var lastTriggerTime: Long,

    /**
     * 下次执行时间
     */
    var nextTriggerTime: Long,
    /**
     * 资源内容，可以是脚本，也可以是yaml
     * */
    var source: String? = null,
    /**
     * 镜像地址
     * */
    var image: String? = null,
) {
    companion object {
        const val NEXT_TRIGGER_TIME_IDX = "nextTriggerTime_idx"
        const val NEXT_TRIGGER_TIME_IDX_DEF = "{'nextTriggerTime': 1}"
    }
}
