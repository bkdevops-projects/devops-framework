

description = "Tencent BlueKing DevOps Framework Build"

plugins {
    kotlin("jvm") version Versions.Kotlin apply false
    kotlin("kapt") version Versions.Kotlin apply false
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement apply false
    id("io.github.gradle-nexus.publish-plugin") version Versions.GradleNexusPublish
}

allprojects {
    group = Release.Group
    version = Release.Version

    repositories {
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

subprojects {
    apply(plugin = "ktlint")
}
