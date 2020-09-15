package com.tencent.devops

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URI
import java.util.jar.Attributes
import java.util.jar.JarFile


/**
 * DevOps Boot Gradle插件，提供公共配置
 */
class DevOpsBootPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        this.configureKotlinCompileConventions(project)
        this.configureTestConventions(project)
        this.configureRepository(project)
        this.configureSpringGradlePlugin(project)
        this.configureDependencyManagement(project)
        this.configureDependency(project)
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
            tasks.withType(JavaCompile::class.java) {
                it.sourceCompatibility = JavaVersion.VERSION_1_8.toString()
                it.options.encoding = "UTF-8"
            }
        }
    }

    /**
     * 配置test任务选项
     */
    private fun configureTestConventions(project: Project) {
        project.tasks.withType(Test::class.java) {
            it.useJUnitPlatform()
        }
    }

    /**
     * 配置maven仓库列表
     */
    private fun configureRepository(project: Project) {
        with(project.repositories) {
            maven { it.url = URI("https://mirrors.tencent.com/nexus/repository/maven-public/") }
            mavenCentral()
            jcenter()
            maven { it.url = URI("https://repo.spring.io/libs-milestone") }
        }
    }

    /**
     * 配置Spring Gradle相关插件
     */
    private fun configureSpringGradlePlugin(project: Project) {
        project.plugins.apply(SpringBootPlugin::class.java)
        project.plugins.apply(SpringGradleSubplugin::class.java)
    }

    /**
     * 配置依赖管理插件
     */
    private fun configureDependencyManagement(project: Project) {
        project.plugins.apply(DependencyManagementPlugin::class.java)
        project.extensions.findByType(DependencyManagementExtension::class.java)?.imports {
            it.mavenBom(BOM_COORDINATES)
        }
    }

    /**
     * 配置默认依赖
     */
    private fun configureDependency(project: Project) {
        with(project.dependencies) {
            add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
        }
    }

    companion object {
        /**
         * DevOps Boot 版本号
         */
        private val DEVOPS_BOOT_VERSION: String? = determineDevOpsBootVersion()

        /**
         * DevOps BOM 文件坐标
         */
        private val BOM_COORDINATES = "com.tencent.devops:devops-boot-dependencies:$DEVOPS_BOOT_VERSION"

        /**
         * 判断`DevOpsBoot`版本
         */
        private fun determineDevOpsBootVersion(): String? {
            val implementationVersion = this::class.java.getPackage().implementationVersion
            if (implementationVersion != null) {
                return implementationVersion
            }
            val codeSourceLocation = DevOpsBootPlugin::class.java.protectionDomain.codeSource.location
            try {
                val connection = codeSourceLocation.openConnection()
                if (connection is JarURLConnection) {
                    return getImplementationVersion(connection.jarFile)
                }
                JarFile(File(codeSourceLocation.toURI())).use {
                    return getImplementationVersion(it)
                }
            } catch (ex: Exception) {
                return null
            }
        }

        /**
         * 从[jarFile]的`Manifest`文件中获取版本号，需要在[jarFile]打包时写入
         */
        @Throws(IOException::class)
        private fun getImplementationVersion(jarFile: JarFile): String? {
            return jarFile.manifest.mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION)
        }
    }
}

