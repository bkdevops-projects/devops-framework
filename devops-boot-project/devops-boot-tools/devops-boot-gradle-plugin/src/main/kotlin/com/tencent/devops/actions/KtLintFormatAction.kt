package com.tencent.devops.actions

import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec

/**
 * ktlint format action
 */
class KtLintFormatAction(
    private val ktLint: FileCollection
) : Action<JavaExec> {

    override fun execute(javaExec: JavaExec) {
        with(javaExec) {
            val outputDir = "${project.buildDir}/reports/ktlint/"
            val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))
            group = "formatting"
            inputs.files(inputFiles)
            outputs.dir(outputDir)
            description = "Fix Kotlin code style deviations."
            classpath = ktLint
            main = "com.pinterest.ktlint.Main"
            args = listOf("-F", "src/**/*.kt")
        }
    }

    companion object {
        const val TASK_NAME = "ktlintFormat"
    }
}
