package com.tencent.devops.schedule.pojo.job

data class JobUpdateRequest(
    /**
     * 任务id
     */
    val id: String,

    /**
     * 描述
     */
    val description: String? = null,

    /**
     * 所属工作组
     */
    val groupId: String? = null,

    /**
     * 执行器模式
     */
    var jobMode: Int? = null,

    /**
     * 执行器名称
     */
    val jobHandler: String? = null,

    /**
     * 任务参数
     */
    var jobParam: String? = null,

    /**
     * 任务超时时间，单位秒，大于0生效
     */
    var jobTimeout: Int? = null,

    /**
     * 过期处理策略
     */
    val misfireStrategy: Int? = null,

    /**
     * 路由策略
     */
    val routeStrategy: Int? = null,

    /**
     * 阻塞处理策略
     */
    val blockStrategy: Int? = null,

    /**
     * 最大重试次数
     */
    val maxRetryCount: Int? = null,
)
