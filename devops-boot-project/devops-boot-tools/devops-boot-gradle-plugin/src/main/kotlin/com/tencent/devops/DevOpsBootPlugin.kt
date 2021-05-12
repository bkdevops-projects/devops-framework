package com.tencent.devops

import com.tencent.devops.conventions.JUnitConvention
import com.tencent.devops.conventions.JavaConvention
import com.tencent.devops.conventions.KotlinConvention
import com.tencent.devops.conventions.SpringBootConvention
import com.tencent.devops.utils.DevOpsVersionExtractor
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI
import java.nio.file.Files

/**
 * DevOps Boot Gradle插件，提供公共配置
 */
class DevOpsBootPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        this.configureRepository(project)
        this.configureDependencyManagement(project)
        // ignore the next configuration if this is an empty project
        if (isNotEmptyProject(project)) {
            JavaConvention().apply(project)
            KotlinConvention().apply(project)
            SpringBootConvention().apply(project)
            JUnitConvention().apply(project)
        }
    }

    /**
     * 配置maven仓库列表
     */
    private fun configureRepository(project: Project) {
        project.repositories.run {
            mavenLocal()
            maven { it.url = URI("https://mirrors.tencent.com/nexus/repository/maven-public/") }
            mavenCentral()
            jcenter()
            maven { it.url = URI("https://repo.spring.io/libs-milestone") }
            maven { it.url = URI("https://oss.sonatype.org/content/repositories/snapshots/") }
        }
    }

    /**
     * 配置依赖管理
     */
    private fun configureDependencyManagement(project: Project) {
        project.run {
            pluginManager.apply(DependencyManagementPlugin::class.java)
            extensions.findByType(DependencyManagementExtension::class.java)?.imports {
                it.mavenBom(BOM_COORDINATES)
            }
        }
    }

    /**
     * 是否为非空项目
     */
    private fun isNotEmptyProject(project: Project): Boolean {
        return Files.exists(project.projectDir.toPath().resolve("src"))
    }

    companion object {

        /**
         * DevOps Boot 版本号
         */
        private val DEVOPS_BOOT_VERSION: String = DevOpsVersionExtractor.extractVersion().orEmpty()

        /**
         * DevOps BOM 文件坐标
         */
        private val BOM_COORDINATES = "com.tencent.devops:devops-boot-dependencies:$DEVOPS_BOOT_VERSION"
    }
}
