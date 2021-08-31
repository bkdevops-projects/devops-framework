package com.tencent.devops.conventions

import com.tencent.devops.utils.findJavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import java.nio.charset.StandardCharsets.UTF_8

/**
 * java相关配置
 * - 添加Java插件
 * - 配置默认编译选项
 */
class JavaConvention {

    fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JavaPlugin::class.java)
            tasks.withType(JavaCompile::class.java) {
                it.sourceCompatibility = findJavaVersion(this)
                it.options.encoding = UTF_8.name()
            }
        }
    }
}
