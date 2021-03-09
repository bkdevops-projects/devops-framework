package com.tencent.devops.actions

import com.tencent.devops.findPropertyOrEmpty
import org.gradle.api.Action
import org.gradle.api.tasks.Copy
import org.springframework.boot.gradle.tasks.bundling.BootJar

/**
 * copy to release action
 */
class CopyToReleaseAction: Action<Copy> {

    override fun execute(copy: Copy) {
        with(copy) {
            val withVersion = project.findPropertyOrEmpty(COPY_WITH_VERSION).toBoolean()
            val bootJar = project.tasks.withType(BootJar::class.java).first()
            from(bootJar.archiveFile)
            into("${project.rootDir}/release")
            if (!withVersion) {
                rename { it.replace("-${project.version}", "") }
            }
            outputs.upToDateWhen { false }
        }
    }

    companion object {
        const val TASK_NAME = "copyToRelease"
        private const val COPY_WITH_VERSION = "devops.copyWithVersion"
    }
}
