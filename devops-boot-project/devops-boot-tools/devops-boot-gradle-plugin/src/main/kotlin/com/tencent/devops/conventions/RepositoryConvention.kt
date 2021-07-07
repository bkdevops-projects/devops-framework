package com.tencent.devops.conventions

import com.tencent.devops.utils.DevOpsVersionExtractor
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
            maven {
                it.name = "TencentMirrors"
                it.url = URI("https://mirrors.tencent.com/nexus/repository/maven-public/")
            }
            mavenCentral()
            jcenter()
            maven {
                it.name = "MavenSnapshotRepo"
                it.url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
            }
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
