package com.tencent.devops

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URI
import java.nio.file.Files
import java.util.jar.Attributes
import java.util.jar.JarFile

/**
 * DevOps Boot Gradle插件，提供公共配置
 */
class DevOpsBootPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        this.configureRepository(project)
        this.configureDependencyManagement(project)
        // ignore the next configuration if this is an empty project
        if (isNotEmptyProject(project)) {
            this.configureJavaSupport(project)
            this.configureKotlinSupport(project)
            this.configureSpringBootSupport(project)
            this.configureJUnitTest(project)
        }
    }

    /**
     * 配置maven仓库列表
     */
    private fun configureRepository(project: Project) {
        project.repositories.run {
            mavenLocal()
            maven { it.url = URI("https://mirrors.tencent.com/nexus/repository/maven-public/") }
            mavenCentral()
            jcenter()
            maven { it.url = URI("https://repo.spring.io/libs-milestone") }
            maven { it.url = URI("https://oss.sonatype.org/content/repositories/snapshots/") }
        }
    }

    /**
     * 配置默认编译选项
     */
    private fun configureJavaSupport(project: Project) {
        project.run {
            pluginManager.apply(JavaPlugin::class.java)
            tasks.withType(JavaCompile::class.java) {
                it.sourceCompatibility = findJavaVersion(this)
                it.options.encoding = "UTF-8"
            }
        }
    }

    /**
     * 配置Kotlin默认编译选项
     */
    private fun configureKotlinSupport(project: Project) {
        project.run {
            if (!isKotlinSupport(this)) {
                return
            }
            pluginManager.apply(KotlinPlatformJvmPlugin::class.java)
            // all-open kotlin class
            pluginManager.apply(SpringGradleSubplugin::class.java)
            tasks.withType(KotlinCompile::class.java) {
                it.kotlinOptions.jvmTarget = findJavaVersion(this)
                it.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            }
            dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
            configureKtLint(this)
        }
    }

    /**
     * 配置test任务选项
     */
    private fun configureJUnitTest(project: Project) {
        project.run {
            tasks.withType(Test::class.java) {
                it.useJUnitPlatform()
            }
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test").apply {
                (this as ModuleDependency).exclude(mapOf("group" to "org.junit.vintage", "module" to "junit-vintage-engine"))
            }
        }
    }

    /**
     * 配置依赖管理
     */
    private fun configureDependencyManagement(project: Project) {
        project.run {
            pluginManager.apply(DependencyManagementPlugin::class.java)
            extensions.findByType(DependencyManagementExtension::class.java)?.imports {
                it.mavenBom(BOM_COORDINATES)
            }
        }
    }

    /**
     * 配置Spring Boot Gradle插件以及相关配置
     * reference: https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/
     */
    private fun configureSpringBootSupport(project: Project) {
        if (isBootProject(project)) {
            project.pluginManager.apply(SpringBootPlugin::class.java)
            configureCopyToRelease(project)
        }
    }

    /**
     * 配置ktlint task
     */
    private fun configureKtLint(project: Project) {
        project.run {
            val ktLint = configurations.create("ktlint")
            dependencies.add("ktlint", "com.pinterest:ktlint:0.37.2")

            val outputDir = "$buildDir/reports/ktlint/"
            val inputFiles = fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

            tasks.create("ktlintCheck", JavaExec::class.java) {
                it.group = "verification"
                it.inputs.files(inputFiles)
                it.outputs.dir(outputDir)
                it.description = "Check Kotlin code style."
                it.classpath = ktLint
                it.main = "com.pinterest.ktlint.Main"
                it.args = listOf("src/**/*.kt")
            }

            tasks.create("ktlintFormat", JavaExec::class.java) {
                it.group = "formatting"
                it.inputs.files(inputFiles)
                it.outputs.dir(outputDir)
                it.description = "Fix Kotlin code style deviations."
                it.classpath = ktLint
                it.main = "com.pinterest.ktlint.Main"
                it.args = listOf("-F", "src/**/*.kt")
            }
        }
    }

    /**
     * 配置copyToRelease task
     */
    private fun configureCopyToRelease(project: Project) {
        project.run {
            val copyToRelease = tasks.register("copyToRelease", Copy::class.java) { copy ->
                copy.from("build/libs") {
                    it.include("**/*.jar")
                }
                copy.into("${project.rootDir}/release")
                copy.outputs.upToDateWhen { false }
            }
            tasks.getByName("build").dependsOn(copyToRelease)
        }
    }

    /**
     * 查找java version, 默认1.8
     */
    private fun findJavaVersion(project: Project): String {
        return if (project.hasProperty(DEVOPS_JAVA_VERSION)) {
            project.property(DEVOPS_JAVA_VERSION).toString()
        } else {
            JavaVersion.VERSION_1_8.toString()
        }
    }

    /**
     * 查找是否配置kotlin支持，默认开启
     */
    private fun isKotlinSupport(project: Project): Boolean {
        return if (project.hasProperty(DEVOPS_KOTLIN)) {
            project.property(DEVOPS_KOTLIN).toString().toBoolean()
        } else true
    }

    /**
     * 是否为Boot项目，即包含了被@SpringBootApplication注解的MainClass项目
     */
    private fun isBootProject(project: Project): Boolean {
        return project.name.startsWith("boot-")
    }

    /**
     * 是否为非空项目
     */
    private fun isNotEmptyProject(project: Project): Boolean {
        return Files.exists(project.projectDir.toPath().resolve("src"))
    }

    companion object {
        /**
         * DevOps Java 版本号
         */
        private const val DEVOPS_JAVA_VERSION = "devops.javaVersion"

        /**
         * DevOps Java 版本号
         */
        private const val DEVOPS_KOTLIN = "devops.kotlin"

        /**
         * DevOps Boot 版本号
         */
        private val DEVOPS_BOOT_VERSION: String = determineDevOpsBootVersion().orEmpty()

        /**
         * DevOps BOM 文件坐标
         */
        private val BOM_COORDINATES = "com.tencent.devops:devops-boot-dependencies:$DEVOPS_BOOT_VERSION"

        /**
         * 判断`DevOpsBoot`版本
         */
        private fun determineDevOpsBootVersion(): String? {
            val implementationVersion = this::class.java.getPackage().implementationVersion
            if (implementationVersion != null) {
                return implementationVersion
            }
            val codeSourceLocation = DevOpsBootPlugin::class.java.protectionDomain.codeSource.location
            try {
                val connection = codeSourceLocation.openConnection()
                if (connection is JarURLConnection) {
                    return getImplementationVersion(connection.jarFile)
                }
                JarFile(File(codeSourceLocation.toURI())).use {
                    return getImplementationVersion(it)
                }
            } catch (ex: Exception) {
                return null
            }
        }

        /**
         * 从[jarFile]的`Manifest`文件中获取版本号，需要在[jarFile]打包时写入
         */
        @Throws(IOException::class)
        private fun getImplementationVersion(jarFile: JarFile): String? {
            return jarFile.manifest.mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION)
        }
    }
}
