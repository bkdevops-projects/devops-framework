package com.tencent.devops.conventions

import com.tencent.devops.utils.DevOpsVersionExtractor
import com.tencent.devops.utils.findPropertyOrNull
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Project
import java.net.URI

/**
 * 仓库&依赖相关配置
 */
class RepositoryConvention {

    fun apply(project: Project) {
        configureRepository(project)
        configureDependencyManagement(project)
    }

    /**
     * 配置maven仓库列表
     */
    private fun configureRepository(project: Project) {
        with(project.repositories) {
            // customize
            project.findPropertyOrNull("mavenRepoUrl")?.let { url ->
                maven { it.url = URI(url) }
            }
            // release
            if (System.getenv("GITHUB_WORKFLOW") == null) {
                maven { it.url = URI("https://mirrors.tencent.com/nexus/repository/maven-public") }
                maven { it.url = URI("https://mirrors.tencent.com/nexus/repository/gradle-plugins/") }
            } else {
                mavenCentral()
                gradlePluginPortal()
            }
            // spring
            maven { it.url = URI("https://repo.spring.io/milestone") }
            // snapshot
            maven {
                it.name = "MavenSnapshot"
                it.url = URI("https://central.sonatype.com/content/repositories/snapshots/")
                it.mavenContent { descriptor ->
                    descriptor.snapshotsOnly()
                }
            }
            mavenLocal()
        }
    }

    /**
     * 配置依赖管理
     */
    private fun configureDependencyManagement(project: Project) {
        with(project) {
            pluginManager.apply(DependencyManagementPlugin::class.java)
            extensions.findByType(DependencyManagementExtension::class.java)?.imports {
                it.mavenBom(BOM_COORDINATES)
            }
        }
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
