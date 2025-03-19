package com.tencent.devops.conventions

import com.tencent.devops.actions.KtLintCheckAction
import com.tencent.devops.actions.KtLintFormatAction
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec

/**
 * ktlint相关配置
 */
class KtLintConvention {

    /**
     * 配置ktlint task
     */
    fun apply(project: Project) {
        with(project) {
            val ktLint = configurations.create(KT_LINT)
            dependencies.apply {
                val dependency = create(KT_LINT_DEPENDENCY) as ExternalModuleDependency
                dependency.attributes {
                    it.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
                }
                add(KT_LINT, dependency)
            }
            tasks.create(KtLintCheckAction.TASK_NAME, JavaExec::class.java, KtLintCheckAction(ktLint))
            tasks.create(KtLintFormatAction.TASK_NAME, JavaExec::class.java, KtLintFormatAction(ktLint))
        }
    }

    companion object {
        private const val KT_LINT = "ktlint"
        private const val KT_LINT_DEPENDENCY = "com.pinterest:ktlint:0.50.0"
    }
}
