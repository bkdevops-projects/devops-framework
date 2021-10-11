package com.tencent.devops.schedule.mongo.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * worker group
 * job工作组，对应一组执行器列表
 */
@Document(collection = "worker_group")
data class TWorkerGroup(

    var id: String? = null,

    /**
     * 工作组名称，全局唯一
     */
    var name: String,

    /**
     * 地址发现策略
     */
    var discoveryType: Int,

    /**
     * 状态更新时间
     */
    var updateTime: LocalDateTime,

    /**
     * 执行器地址列表
     */
    var addressList: String
)
