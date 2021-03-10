package com.tencent.devops

import org.gradle.api.Project

/**
 * 查找属性, 如果[name]不存在则返回null。
 * gradle提供的findProperty方法支持从gradle.property和命令行参数-Pkey=value获取, 后者优先级更高。此扩展方法增加了获取属性的范围
 * 优先级如下：
 * 1. 命令行 -Dkey=value
 * 2. 系统环境变量key=value
 * 3. 命令行 -Pkey=value
 * 4. 配置文件gradle.properties key=value
 */
fun Project.findPropertyOrNull(name: String): String? {
    return System.getProperty(name) ?: System.getenv(name) ?: run {
        if (hasProperty(name)) {
            return property(name).toString()
        } else null
    }
}

/**
 * 查找属性, 如果[name]不存在则返回空字符串
 */
fun Project.findPropertyOrEmpty(name: String): String {
    return findPropertyOrNull(name) ?: ""
}

/**
 * 查找属性, 如果[name]不存在则返回默认值[default]
 */
fun Project.findPropertyOrDefault(name: String, default: String): String {
    return findPropertyOrNull(name) ?: default
}

