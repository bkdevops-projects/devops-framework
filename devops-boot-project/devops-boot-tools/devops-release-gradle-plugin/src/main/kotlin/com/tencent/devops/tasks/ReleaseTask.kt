package com.tencent.devops.tasks

import com.tencent.devops.dsl.DevOpsReleasePluginExtension
import com.tencent.devops.util.ReleasePluginHelper
import org.apache.maven.scm.CommandParameter
import org.apache.maven.scm.CommandParameters
import org.apache.maven.scm.ScmException
import org.apache.maven.scm.ScmFile
import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.ScmResult
import org.apache.maven.scm.ScmTagParameters
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.repository.ScmRepository
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * 发布任务
 * */
open class ReleaseTask @Inject constructor(
    private val manager: BasicScmManager,
) : DefaultTask() {
    private val extension = project.extensions.getByType(DevOpsReleasePluginExtension::class.java)
    private val versionFile = project.rootDir.resolve(VERSION_FILE_NAME)
    private val backupVersionFile = project.rootDir.resolve(BACKUP_VERSION_FILE_NAME)
    private val versionFileSet = ScmFileSet(project.rootDir, versionFile)

    @TaskAction
    fun release() {
        checkRequires()
        val rootSet = ScmFileSet(project.rootDir)
        val scmRepo = manager.makeScmRepository(extension.scmUrl.get())
        val provider = manager.getProviderByType(scmRepo.provider)
        val message = provider.status(scmRepo, rootSet)
        // scm check modifications
        if (message.changedFiles.isNotEmpty()) {
            printFiles(message.changedFiles)
            throw GradleException("You have some changes that have not been submitted.")
        }
        val pluginHelper = ReleasePluginHelper(project)
        val releaseVer = pluginHelper.releaseVersion()
        val devVer = pluginHelper.developmentVersion()
        val rollbackPendingList = mutableListOf<Runnable>()
        try {
            versionFile.copyTo(backupVersionFile, overwrite = true)
            // rewrite for release
            versionFile.writeText(releaseVer)
            // scm commit release
            val releaseMsgPrefix = "[devops-release-plugin] prepare release"
            provider.checkIn(scmRepo, versionFileSet, "$releaseMsgPrefix $releaseVer").requireSuccess()
            rollbackPendingList.add { revertVersionFile(provider, scmRepo, releaseVer) }
            // scm tag
            val scmTagParameters = ScmTagParameters("$releaseMsgPrefix $releaseVer")
            provider.tag(scmRepo, rootSet, pluginHelper.tagName(), scmTagParameters).requireSuccess()
            rollbackPendingList.add { untag(pluginHelper, provider, scmRepo, rootSet) }
            // rewrite for development
            versionFile.writeText(devVer)
            // scm commit development
            val developMsgPrefix = "[devops-release-plugin] prepare development"
            provider.checkIn(scmRepo, versionFileSet, "$developMsgPrefix $devVer").requireSuccess()
        } catch (e: Exception) {
            // rollback on error
            if (rollbackPendingList.isNotEmpty()) {
                rollbackPendingList.forEach { it.run() }
            }
            throw e
        } finally {
            backupVersionFile.delete()
        }
    }

    /**
     * 删除tag
     * */
    private fun untag(
        pluginHelper: ReleasePluginHelper,
        provider: ScmProvider,
        scmRepo: ScmRepository?,
        rootSet: ScmFileSet,
    ) {
        val commandParameters = CommandParameters()
        commandParameters.setString(CommandParameter.TAG_NAME, pluginHelper.tagName())
        commandParameters.setString(
            CommandParameter.MESSAGE,
            "[devops-release-plugin] remove tag ${pluginHelper.tagName()}",
        )
        provider.untag(scmRepo, rootSet, commandParameters)
    }

    /**
     * 恢复版本文件
     * */
    private fun revertVersionFile(
        provider: ScmProvider,
        scmRepo: ScmRepository?,
        releaseVer: String,
    ) {
        backupVersionFile.copyTo(versionFile, overwrite = true)
        provider.checkIn(scmRepo, versionFileSet, "[devops-release-plugin] rollback the release of $releaseVer")
            .requireSuccess()
    }

    /**
     * 检查必要条件
     * */
    private fun checkRequires() {
        requireNotNull(extension.scmUrl.orNull) { "Please set scm url." }
        if (!versionFile.exists()) {
            throw GradleException("Unable find version.txt in project root.")
        }
        val version = versionFile.readText().trim()
        val projectVersion = project.version.toString()
        if (version != projectVersion) {
            throw GradleException(
                "Project version[$projectVersion] not equals [$version] in version.txt," +
                    "please use version.txt for project version.",
            )
        }
    }

    private fun printFiles(fileList: List<ScmFile>) {
        fileList.forEach {
            logger.error("${it.path} ${it.status}")
        }
    }

    private fun ScmResult.requireSuccess() {
        if (!this.isSuccess) {
            throw ScmException("${this.providerMessage}: ${this.commandOutput}")
        }
    }

    companion object {
        private const val VERSION_FILE_NAME = "version.txt"
        private const val BACKUP_VERSION_FILE_NAME = "version.txt_backup"
    }
}
