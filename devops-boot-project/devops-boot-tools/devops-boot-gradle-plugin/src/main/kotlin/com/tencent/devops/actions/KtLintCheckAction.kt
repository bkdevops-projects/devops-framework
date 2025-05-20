package com.tencent.devops.actions

import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec

/**
 * ktlint check action
 */
class KtLintCheckAction(
    private val ktLint: FileCollection
) : Action<JavaExec> {

    override fun execute(javaExec: JavaExec) {
        with(javaExec) {
            val outputDir = "${project.layout.buildDirectory}/reports/ktlint/"
            val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))
            group = "verification"
            inputs.files(inputFiles)
            outputs.dir(outputDir)
            description = "Check Kotlin code style."
            classpath = ktLint
            mainClass.set("com.pinterest.ktlint.Main")
            args = listOf("src/**/*.kt")
        }
    }

    companion object {
        const val TASK_NAME = "ktlintCheck"
    }
}
