package com.tencent.devops

import com.tencent.devops.plugin.common.PropertyUtil.findProperty
import com.tencent.devops.plugin.common.PropertyUtil.findPropertyOrEmpty
import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.io.File
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository

/**
 * DevOps Publish Gradle插件，提供公共配置
 */
@Suppress("UnstableApiUsage")
class DevOpsPublishPlugin : Plugin<Project> {

    private var isReleaseVersion: Boolean = false

    override fun apply(project: Project) {
        isReleaseVersion = !project.version.toString().endsWith("SNAPSHOT")
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        project.pluginManager.apply(SigningPlugin::class.java)
        if (project.rootProject == project) {
            project.pluginManager.apply(NexusPublishPlugin::class.java)
            configureNexusPublish(project)
        }
        project.afterEvaluate {
            configurePublishing(project)
            configureSigning(project)
            configureManifest(project)
        }
    }

    /**
     * 配置maven publish
     */
    private fun configurePublishing(project: Project) {
        project.run {
            extensions.getByType(PublishingExtension::class.java).run {
                publications.run {
                    // jar
                    plugins.findPlugin(JavaPlugin::class.java)?.let {
                        extensions.getByType(JavaPluginExtension::class.java).run {
                            withJavadocJar()
                            withSourcesJar()
                        }
                        create("jar", MavenPublication::class.java) {
                            it.from(components.getByName("java"))
                        }
                    }
                    // pom
                    plugins.findPlugin(JavaPlatformPlugin::class.java)?.let {
                        create("pom", MavenPublication::class.java) {
                            it.from(components.getByName("javaPlatform"))
                        }
                    }
                }
            }
        }
    }

    private fun configureNexusPublish(project: Project) {
        project.run {
            extensions.getByType(NexusPublishExtension::class.java).repositories.sonatype { repo ->
                val releaseUrl = findProperty(project, RELEASE_REPO_URL)
                val snapshotUrl = findProperty(project, SNAPSHOT_REPO_URL)
                releaseUrl?.let { repo.nexusUrl.set(uri(releaseUrl)) }
                snapshotUrl?.let { repo.snapshotRepositoryUrl.set(uri(snapshotUrl)) }
                repo.username.set(findPropertyOrEmpty(project, REPO_USERNAME))
                repo.password.set(findPropertyOrEmpty(project, REPO_PASSWORD))
            }
        }
    }

    /**
     * 配置签名，SonaType规范
     */
    private fun configureSigning(project: Project) {
        project.run {
            extensions.getByType(SigningExtension::class.java).run {
                val signingKey = findProperty(project, SIGNING_KEY)
                val signingKeyFile = findProperty(project, SIGNING_KEY_FILE)
                val signingKeyId = findProperty(project, SIGNING_KEY_ID)
                val signingPassword = findProperty(project, SIGNING_PASSWORD)
                val secretKey: String? = signingKey ?: signingKeyFile?.let { File(it).readText() }
                useInMemoryPgpKeys(signingKeyId, secretKey, signingPassword)
                this.setRequired({ isReleaseVersion && tasks.findByName(PUBLISH_TASK_PATH) != null })
                sign(extensions.getByType(PublishingExtension::class.java).publications)
            }

            tasks.withType(Sign::class.java) {
                it.onlyIf { isReleaseVersion }
            }

            tasks.withType(PublishToMavenRepository::class.java).configureEach { publishTask ->
                val publicationSegment = publishTask.name.removePrefix("publish").substringBefore("PublicationTo")
                val signTaskName = "sign${publicationSegment}Publication"
                tasks.findByName(signTaskName)?.let { publishTask.dependsOn(it) }
            }
        }
    }

    /**
     * 配置manifest, SonaType规范
     */
    private fun configureManifest(project: Project) {
        project.run {
            val manifestMap = mapOf(
                "Implementation-Title" to (project.description ?: project.name),
                "Implementation-Version" to project.version,
            )
            tasks.withType(Jar::class.java) {
                it.manifest { manifest -> manifest.attributes(manifestMap) }
            }
        }
    }

    companion object {
        private const val PUBLISH_TASK_PATH = "publish"
        private const val SIGNING_KEY = "signingKey"
        private const val SIGNING_KEY_FILE = "signingKeyFile"
        private const val SIGNING_KEY_ID = "signingKeyId"
        private const val SIGNING_PASSWORD = "signingPassword"

        private const val RELEASE_REPO_URL = "releaseRepoUrl"
        private const val SNAPSHOT_REPO_URL = "snapshotRepoUrl"
        private const val REPO_USERNAME = "repoUsername"
        private const val REPO_PASSWORD = "repoPassword"
    }
}
