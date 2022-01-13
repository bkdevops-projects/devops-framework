package com.tencent.devops.schedule.mongo.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * worker registry
 * job执行器注册表
 */
@Document(collection = "worker_info")
data class TWorker(

    var id: String? = null,

    /**
     * 地址，格式ip:port
     */
    var address: String,

    /**
     * 工作组名称
     */
    var group: String,

    /**
     * 状态更新时间
     */
    var updateTime: LocalDateTime,
)
