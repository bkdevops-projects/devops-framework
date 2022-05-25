package com.tencent.devops.conventions

import com.tencent.devops.actions.CopyToReleaseAction
import com.tencent.devops.utils.isBootProject
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.springframework.boot.gradle.plugin.SpringBootPlugin

/**
 * 配置Spring Boot Gradle插件以及相关配置
 * reference: https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/
 */
class SpringBootConvention {

    fun apply(project: Project) {
        with(project) {
            if (isBootProject(this)) {
                project.pluginManager.apply(SpringBootPlugin::class.java)
                configureCopyToRelease(this)
                SpringCloudConvention().apply(this)
            }
        }
    }

    /**
     * 配置copyToRelease task
     */
    private fun configureCopyToRelease(project: Project) {
        with(project.tasks) {
            val copyToRelease = register(CopyToReleaseAction.TASK_NAME, Copy::class.java, CopyToReleaseAction())
            named("build").configure { it.dependsOn(copyToRelease) }
        }
    }
}
