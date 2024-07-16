package com.tencent.devops.common.time

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return this.atZone(zoneId).toInstant().toEpochMilli()
}
