package com.tencent.devops.schedule.pojo.worker

import java.time.LocalDateTime

data class WorkerInfo(
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
