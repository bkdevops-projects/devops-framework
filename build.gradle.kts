import de.marcphilipp.gradle.nexus.NexusPublishExtension

description = "Tencent BlueKing DevOps Framework Build"

plugins {
    kotlin("jvm") version Versions.Kotlin apply false
    kotlin("kapt") version Versions.Kotlin apply false
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement apply false
    id("de.marcphilipp.nexus-publish") version Versions.NexusPublish apply false
    id("io.codearte.nexus-staging") version Versions.NexusStaging
}

allprojects {
    group = Release.Group
    version = Release.Version

    repositories {
        mavenCentral()
        jcenter()
    }
}

nexusStaging {
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")
}

subprojects {
    apply(plugin = "de.marcphilipp.nexus-publish")
    apply(plugin = "ktlint")

    configure<NexusPublishExtension> {
        repositories {
            sonatype()
        }
    }
}
