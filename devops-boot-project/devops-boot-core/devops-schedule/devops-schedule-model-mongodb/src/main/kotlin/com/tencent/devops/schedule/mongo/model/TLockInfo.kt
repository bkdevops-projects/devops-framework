package com.tencent.devops.schedule.mongo.model

import org.springframework.data.mongodb.core.mapping.Document

/**
 * 分布式锁模型类
 */
@Document(collection = "distributed_lock")
data class TLockInfo(

    var id: String? = null,

    /**
     * 过期时间
     */
    var expireAt: Long,

    /**
     * 锁 token
     */
    var token: String
)
