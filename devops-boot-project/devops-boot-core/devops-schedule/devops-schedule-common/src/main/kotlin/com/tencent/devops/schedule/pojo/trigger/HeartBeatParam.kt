package com.tencent.devops.schedule.pojo.trigger

data class HeartBeatParam(
    val group: String,
    val address: String,
    val status: Int,
)
