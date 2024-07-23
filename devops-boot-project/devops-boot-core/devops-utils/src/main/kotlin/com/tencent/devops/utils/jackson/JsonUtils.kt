package com.tencent.devops.utils.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.io.InputStream

/**
 * Json工具类
 */
object JsonUtils {
    val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
        registerModule(ParameterNamesModule())
        registerModule(Jdk8Module())

        enable(SerializationFeature.INDENT_OUTPUT)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }
}

/**
 * 将对象序列化为json字符串
 */
fun Any.toJsonString() = JsonUtils.objectMapper.writeValueAsString(this).orEmpty()

/**
 * 将json字符串反序列化为对象
 */
inline fun <reified T> String.readJsonString(): T = JsonUtils.objectMapper.readValue(this, jacksonTypeRef<T>())

/**
 * 将json字符串流反序列化为对象
 */
inline fun <reified T> InputStream.readJsonString(): T = JsonUtils.objectMapper.readValue(this, jacksonTypeRef<T>())
