package com.tencent.devops.schedule.enums

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * [DictItem]自定义序列化
 */
class DictItemSerializable: JsonSerializer<DictItem>() {
    override fun serialize(value: DictItem, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeNumberField("value", value.code())
        gen.writeStringField("label", value.description())
        gen.writeEndObject()
    }
}
