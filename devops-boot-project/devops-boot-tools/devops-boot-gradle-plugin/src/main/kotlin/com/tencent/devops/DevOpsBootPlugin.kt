package com.tencent.devops

import com.tencent.devops.conventions.GoogleJibConvention
import com.tencent.devops.conventions.JUnitConvention
import com.tencent.devops.conventions.JavaConvention
import com.tencent.devops.conventions.KotlinConvention
import com.tencent.devops.conventions.RepositoryConvention
import com.tencent.devops.conventions.SpringBootConvention
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Files

/**
 * DevOps Boot Gradle插件，提供公共配置
 */
class DevOpsBootPlugin : Plugin<Project> {

    //Convention 优先级 : 越往后面的优先级越高
    override fun apply(project: Project) {
        // solve issue 73
        KotlinConvention().apply(project)
        JavaConvention().apply(project)
        // ignore the next configuration if this is an empty project
        if (isNotEmptyProject(project)) {
            SpringBootConvention().apply(project)
            JUnitConvention().apply(project)
        }
        // add dependencyManagement
        RepositoryConvention().apply(project)
        // add jib to build container images
        GoogleJibConvention().apply(project)
    }

    /**
     * 是否为非空项目
     */
    private fun isNotEmptyProject(project: Project): Boolean {
        return Files.exists(project.projectDir.toPath().resolve("src"))
    }
}
