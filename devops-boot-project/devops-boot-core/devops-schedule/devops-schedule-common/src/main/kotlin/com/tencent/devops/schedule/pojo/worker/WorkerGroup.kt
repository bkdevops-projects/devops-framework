package com.tencent.devops.schedule.pojo.worker

import java.time.LocalDateTime

data class WorkerGroup(
    var id: String? = null,

    /**
     * 工作组名称，全局唯一
     */
    var name: String,

    /**
     * 地址发现类型
     */
    var discoveryType: Int,

    /**
     * 状态更新时间
     */
    var updateTime: LocalDateTime,

    /**
     * 执行器地址列表
     */
    var registryList: List<String>
)
