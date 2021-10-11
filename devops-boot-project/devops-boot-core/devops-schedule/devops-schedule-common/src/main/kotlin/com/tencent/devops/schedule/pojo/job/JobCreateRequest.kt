package com.tencent.devops.schedule.pojo.job

data class JobCreateRequest(
    /**
     * 任务名称
     */
    val name: String,

    /**
     * 描述
     */
    val description: String? = null,

    /**
     * 所属工作组
     */
    val groupId: String,

    /**
     * 调度类型
     */
    val scheduleType: Int,

    /**
     * 调度配置，其含义取决于调度类型
     */
    val scheduleConf: String,

    /**
     * 执行器模式
     */
    var jobMode: Int,

    /**
     * 执行器名称
     */
    val jobHandler: String,

    /**
     * 任务参数
     */
    var jobParam: String? = null,

    /**
     * 任务超时时间，单位秒，大于0生效
     */
    var jobTimeout: Int,

    /**
     * 过期处理策略
     */
    val misfireStrategy: Int,

    /**
     * 路由策略
     */
    val routeStrategy: Int,

    /**
     * 阻塞处理策略
     */
    val blockStrategy: Int,

    /**
     * 最大重试次数
     */
    val maxRetryCount: Int
)
