package com.tencent.devops.schedule.enums

import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * 阻塞处理策略
 */
@JsonSerialize(using = DictItemSerializable::class)
interface DictItem {
    fun code(): Int
    fun description(): String
}
