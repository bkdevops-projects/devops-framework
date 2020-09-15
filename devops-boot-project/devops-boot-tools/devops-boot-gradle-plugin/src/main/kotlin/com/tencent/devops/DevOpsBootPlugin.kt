package com.tencent.devops

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * DevOps Boot Gradle插件，提供如下配置：
 *
 * 1. 设置编译选项版本
 * 2. 设置公共依赖
 */
class DevOpsBootPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.setProperty("sourceCompatibility", JavaVersion.VERSION_1_8.toString())
        this.configureKotlinCompileConventions(project)
        this.configureTestConventions(project)
//        this.configureDependencyManagement(project)
    }

    /**
     * 配置Kotlin默认编译选项
     */
    private fun configureKotlinCompileConventions(project: Project) {
        with(project) {
            plugins.apply(KotlinPlatformJvmPlugin::class.java)
            tasks.withType(KotlinCompile::class.java) {
                it.kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
                it.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            }
            tasks.withType(JavaCompile::class.java) { compile ->
                compile.options.encoding = "UTF-8"
            }
        }
    }

    private fun configureTestConventions(project: Project) {
        with(project) {
            tasks.withType(Test::class.java) {
                it.useJUnitPlatform()
            }
        }
    }

    private fun configureDependencyManagement(project: Project) {
        with(project) {
            val dependencyManagement = configurations.create("dependencyManagement") {
                it.isVisible = false
                it.isCanBeConsumed = false
                it.isCanBeResolved = false
            }
            val entry = mapOf("path" to ":devops-boot-project:devops-boot-dependencies")
            val devOpsBootParent = dependencies.platform(dependencies.project(entry))
            dependencyManagement.dependencies.add(devOpsBootParent)
        }
    }
}