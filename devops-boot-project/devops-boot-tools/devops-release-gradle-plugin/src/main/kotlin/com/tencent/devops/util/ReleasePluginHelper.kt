package com.tencent.devops.util

import com.tencent.devops.dsl.DevOpsReleasePluginExtension
import com.tencent.devops.plugin.common.PropertyUtil.findPropertyOrEmpty
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.semver.Version

/**
 * 发布插件助手
 * */
class ReleasePluginHelper(private val project: Project) {
    private val extension = project.extensions.getByType(DevOpsReleasePluginExtension::class.java)

    /**
     * 获取当前版本
     * */
    fun currentVersion(): String {
        return Version.parse(project.version.toString())
            .toString()
    }

    /**
     * 获取发布版本
     * */
    fun releaseVersion(): String {
        val specificVersion = findPropertyOrEmpty(project, "release.releaseVersion")
        if (specificVersion.isNotEmpty()) {
            return specificVersion
        }
        val releaseVersion = currentVersion().removeSuffix(extension.snapshotSuffix.get())
        return Version.parse(releaseVersion).toString()
    }

    /**
     * 获取开发版本
     * */
    fun developmentVersion(): String {
        val specificVersion = findPropertyOrEmpty(project, "release.developmentVersion")
        if (specificVersion.isNotEmpty()) {
            return specificVersion
        }
        val policyStr = extension.incrementPolicy.get()
        if (!availablePolicy.contains(policyStr)) {
            throw GradleException("Invalid incrementPolicy $policyStr.")
        }
        val policy = Version.Element.valueOf(policyStr)
        return Version.parse(releaseVersion())
            .next(policy)
            .toString()
            .plus(extension.snapshotSuffix.get())
    }

    /**
     * 获取标签名
     * */
    fun tagName(): String {
        val specificTagName = findPropertyOrEmpty(project, "release.tagName")
        if (specificTagName.isNotEmpty()) {
            return specificTagName
        }
        return "v${releaseVersion()}"
    }

    companion object {
        val availablePolicy = listOf("MAJOR", "MINOR", "PATCH")
    }
}
