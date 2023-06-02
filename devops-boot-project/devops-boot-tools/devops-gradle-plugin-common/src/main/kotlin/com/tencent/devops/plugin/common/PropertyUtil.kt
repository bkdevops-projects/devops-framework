package com.tencent.devops.plugin.common

import org.gradle.api.Project

object PropertyUtil {
    private const val EMPTY = ""

    /**
     * 查找属性, 如果[name]不存在则返回空字符串 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 配置文件gradle.properties key=value
     * 3. 系统环境变量key=value
     */
    fun findPropertyOrEmpty(project: Project, name: String): String {
        return findProperty(project, name) ?: EMPTY
    }

    /**
     * 查找属性, 如果[name]不存在则返回默认值[default] 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 系统环境变量key=value
     * 3. 配置文件gradle.properties key=value
     */
    fun findPropertyOrDefault(project: Project, name: String, default: String): String {
        return findProperty(project, name) ?: default
    }

    /**
     * 查找属性, 如果[name]不存在则返回null 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 系统环境变量key=value
     * 3. 配置文件gradle.properties key=value
     */
    fun findProperty(project: Project, name: String): String? {
        return System.getProperty(name) ?: System.getenv(name) ?: run {
            if (project.hasProperty(name)) {
                return project.property(name).toString()
            } else {
                null
            }
        }
    }
}
