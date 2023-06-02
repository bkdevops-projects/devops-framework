package com.tencent.devops

import com.tencent.devops.dsl.DevOpsReleasePluginExtension
import com.tencent.devops.tasks.ReleaseTask
import com.tencent.devops.util.ReleasePluginHelper
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.semver.Version

/**
 * DevOps发布插件
 *
 * 管理项目版本，自动的进行发布，更新版本，并通过scm进行版本管理。
 * */
class DevOpsReleasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("release", DevOpsReleasePluginExtension::class.java)
        val pluginHelper = ReleasePluginHelper(project)
        extension.snapshotSuffix.convention(DEFAULT_SNAPSHOT_SUFFIX)
        extension.incrementPolicy.convention(Version.Element.MINOR.name)
        val manager = BasicScmManager()
        manager.setScmProvider("git", GitExeScmProvider())
        project.tasks.register("release", ReleaseTask::class.java, manager).get().apply {
            group = GROUP_NAME
            description = "Manage project version,automatically perform releases, update, and version control."
        }

        project.task("generateReleaseProperties").apply {
            group = GROUP_NAME
            description = "Generate a release.properties file that contains some information related to the release."
            doLast {
                val propertiesFile = project.rootDir.resolve("release.properties")
                if (propertiesFile.exists()) {
                    propertiesFile.delete()
                }
                propertiesFile.createNewFile()
                propertiesFile.apply {
                    appendText("version=${pluginHelper.currentVersion()}\n")
                    appendText("release.version=${pluginHelper.releaseVersion()}\n")
                    appendText("next.development.version=${pluginHelper.developmentVersion()}\n")
                    appendText("tag.name=${pluginHelper.tagName()}\n")
                    appendText("scm.url=${extension.scmUrl.get()}")
                }
            }
        }
    }

    companion object {
        private const val GROUP_NAME = "release"
        private const val DEFAULT_SNAPSHOT_SUFFIX = "-SNAPSHOT"
    }
}
