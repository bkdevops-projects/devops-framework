package com.tencent.devops.utils.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream

/**
 * yaml工具类
 */
object YamlUtils {
    val objectMapper: ObjectMapper = YAMLMapper().apply {
        registerKotlinModule()
        enable(SerializationFeature.INDENT_OUTPUT)
        // 缺省文件以三个横杠开头 禁用该属性
        disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
}

/**
 * 将对象序列化为yaml字符串
 */
fun Any.toYamlString() = YamlUtils.objectMapper.writeValueAsString(this).orEmpty()

/**
 * 将yaml字符串反序列化为对象
 */
inline fun <reified T> String.readYamlString(): T = YamlUtils.objectMapper.readValue(this, jacksonTypeRef<T>())

/**
 * 将yaml字符串流反序列化为对象
 */
inline fun <reified T> InputStream.readYamlString(): T = YamlUtils.objectMapper.readValue(this, jacksonTypeRef<T>())
