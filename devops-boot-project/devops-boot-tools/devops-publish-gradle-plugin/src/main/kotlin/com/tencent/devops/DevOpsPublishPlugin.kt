package com.tencent.devops

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
        configurePublishing(project)
        configureSigning(project)
        configureManifest(project)
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
                repositories.run {
                    maven {
                        it.name = "remote"
                        val url = if (isReleaseVersion) {
                            findPropertyOrDefault(project, RELEASE_REPO_URL, SONATYPE_RELEASE_REPO)
                        } else {
                            findPropertyOrDefault(project, SNAPSHOT_REPO_URL, SONATYPE_SNAPSHOT_REPO)
                        }
                        it.url = uri(url)
                        it.credentials { credentials ->
                            credentials.username = findPropertyOrEmpty(project, REPO_USERNAME)
                            credentials.password = findPropertyOrEmpty(project, REPO_PASSWORD)
                        }
                    }
                }
            }
        }
    }

    /**
     * 配置签名，SonaType规范
     */
    private fun configureSigning(project: Project) {
        project.run {
            afterEvaluate {
                extensions.getByType(SigningExtension::class.java).run {
                    val signingKey = findPropertyOrEmpty(project, SIGNING_KEY)
                    val signingKeyId = findPropertyOrEmpty(project, SIGNING_KEY_ID)
                    val signingPassword = findPropertyOrEmpty(project, SIGNING_PASSWORD)
                    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                    this.setRequired({ isReleaseVersion && gradle.taskGraph.hasTask(PUBLISH_TASK_PATH) })
                    sign(extensions.getByType(PublishingExtension::class.java).publications)
                }

                tasks.withType(Sign::class.java) {
                    it.onlyIf { isReleaseVersion }
                }
            }
        }
    }

    /**
     * 配置manifest, SonaType规范
     */
    private fun configureManifest(project: Project) {
        project.run {
            afterEvaluate {
                val manifestMap = mapOf(
                    "Implementation-Title" to (project.description ?: project.name),
                    "Implementation-Version" to project.version
                )
                tasks.withType(Jar::class.java) {
                    it.manifest { manifest -> manifest.attributes(manifestMap) }
                }
            }
        }
    }

    /**
     * 查找属性, 如果[name]不存在则返回空字符串 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 配置文件gradle.properties key=value
     * 3. 系统环境变量key=value
     */
    private fun findPropertyOrEmpty(project: Project, name: String): String {
        return findProperty(project, name) ?: EMPTY
    }

    /**
     * 查找属性, 如果[name]不存在则返回默认值[default] 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 系统环境变量key=value
     * 3. 配置文件gradle.properties key=value
     */
    private fun findPropertyOrDefault(project: Project, name: String, default: String): String {
        return findProperty(project, name) ?: default
    }

    /**
     * 查找属性, 如果[name]不存在则返回null 优先级如下：
     * 1. 命令行 -Dkey=value
     * 2. 系统环境变量key=value
     * 3. 配置文件gradle.properties key=value
     */
    private fun findProperty(project: Project, name: String): String? {
        return System.getProperty(name) ?: System.getenv(name) ?: run {
            if (project.hasProperty(name)) {
                return project.property(name).toString()
            } else null
        }
    }

    companion object {
        private const val EMPTY = ""
        private const val PUBLISH_TASK_PATH = "publish"
        private const val SIGNING_KEY = "signingKey"
        private const val SIGNING_KEY_ID = "signingKeyId"
        private const val SIGNING_PASSWORD = "signingPassword"

        private const val RELEASE_REPO_URL = "releaseRepoUrl"
        private const val SNAPSHOT_REPO_URL = "snapshotRepoUrl"
        private const val REPO_USERNAME = "repoUsername"
        private const val REPO_PASSWORD = "repoPassword"
        private const val SONATYPE_RELEASE_REPO = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        private const val SONATYPE_SNAPSHOT_REPO = "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}
