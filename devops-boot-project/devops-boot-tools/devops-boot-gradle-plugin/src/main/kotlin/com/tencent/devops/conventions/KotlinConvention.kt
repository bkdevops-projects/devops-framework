package com.tencent.devops.conventions

import com.tencent.devops.utils.findJavaVersion
import com.tencent.devops.utils.isKotlinSupport
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.utils.IMPLEMENTATION

/**
 * kotlin相关配置
 */
class KotlinConvention {
    /**
     * 配置Kotlin默认编译选项
     */
    fun apply(project: Project) {
        with(project) {
            if (!isKotlinSupport(this)) {
                return
            }
            pluginManager.apply(KotlinPluginWrapper::class.java)
            // all-open kotlin class
            pluginManager.apply(SpringGradleSubplugin::class.java)
            tasks.withType(KotlinCompile::class.java) {
                it.compilerOptions.jvmTarget.set(JvmTarget.fromTarget(findJavaVersion(this)))
                it.compilerOptions.freeCompilerArgs.addAll(listOf("-Xjsr305=strict", "-java-parameters"))
            }
            dependencies.add(IMPLEMENTATION, KOTLIN_STDLIB)
            dependencies.add(IMPLEMENTATION, KOTLIN_REFLECT)
            KtLintConvention().apply(this)
        }
    }

    companion object {
        /**
         * std-jdk8依赖
         */
        private const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"

        /**
         * kotlin-reflect依赖
         */
        private const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect"
    }
}
