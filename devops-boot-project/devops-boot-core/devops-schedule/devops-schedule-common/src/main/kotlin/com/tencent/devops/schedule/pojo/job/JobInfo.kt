package com.tencent.devops.schedule.pojo.job

import java.time.LocalDateTime

/**
 * 任务信息
 */
data class JobInfo(

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
     * 调度类型
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
     * 执行模式
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
    var lastTriggerTime: Long = 0,

    /**
     * 下次执行时间
     */
    var nextTriggerTime: Long = 0,
)
