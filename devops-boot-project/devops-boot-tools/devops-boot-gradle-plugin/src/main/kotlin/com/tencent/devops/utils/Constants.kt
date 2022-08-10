package com.tencent.devops.utils

import com.tencent.devops.conventions.SpringCloudConvention
import com.tencent.devops.enums.AssemblyMode
import org.gradle.api.JavaVersion
import org.gradle.api.Project

/**
 * DevOps Java 版本号
 */
const val DEVOPS_JAVA_VERSION = "devops.javaVersion"

/**
 * 是否开启kotlin支持
 */
const val DEVOPS_KOTLIN = "devops.kotlin"

/**
 * 查找java version, 默认1.8
 */
fun findJavaVersion(project: Project): String {
    return project.findPropertyOrDefault(DEVOPS_JAVA_VERSION, JavaVersion.VERSION_1_8.toString())
}

/**
 * 查找是否配置kotlin支持，默认开启
 */
fun isKotlinSupport(project: Project): Boolean {
    return project.findPropertyOrNull(DEVOPS_KOTLIN)?.toBoolean() ?: true
}

/**
 * 是否为Boot项目，即包含了被@SpringBootApplication注解的MainClass项目
 * SpringBoot gradle插件通过asm解析编译后的class文件，寻找被SpringBootApplication注解的MainClass，以此作为启动MainClass；
 * 和SpringBoot不同的是，我们需要在编译之前确定项目是否包含了被@SpringBootApplication注解的MainClass，此时只有源码文件。
 * 一个暴力的方法就是读取源码文件，根据字符串判读，但是这种方式不能得到完全准确的结果，效果也带磋商。
 * 目前使用一种约定俗成的办法，boot启动项目名称必须以boot-开头
 */
fun isBootProject(project: Project): Boolean {
    return project.name.startsWith("boot-") || project.findPropertyOrEmpty("devops.boot") == "true"
}

/**
 * 查找属性, 如果[name]不存在则返回null
 * gradle提供的findProperty方法支持从gradle.property和命令行参数-Pkey=value获取, 后者优先级更高。
 * 此扩展方法增加了获取属性的范围，优先级如下：
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

/**
 * 判断构建模式
 */
fun resolveAssemblyMode(project: Project): AssemblyMode {
    val property = project.findPropertyOrEmpty("devops.assemblyMode").trim()
    return AssemblyMode.ofValueOrDefault(property).apply {
        println("Project[${project.name}] assembly mode: $this")
    }
}
