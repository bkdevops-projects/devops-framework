package com.tencent.devops.schedule.pojo.worker

data class WorkerGroupCreateRequest(
    val name: String,
    val discoveryType: Int,
    val description: String? = null,
)
