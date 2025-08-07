
rootProject.name = "devops-boot-sample"


// for debug devops-boot locally
@Suppress("UnstableApiUsage")
pluginManagement {
    val projectVersion = File(rootDir.parent,"version.txt").readText().trim()
    plugins {
        id("com.tencent.devops.boot") version projectVersion
    }
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://central.sonatype.com/content/repositories/snapshots/")
        }
    }
}

include("api-kotlin-sample")
include("biz-kotlin-sample")
include("boot-java-sample")
include("boot-kotlin-sample")
include("plugin-printer")
include("boot-schedule-server-sample")
include("boot-schedule-worker-cloud-sample")
include("webflux-sample:api-webflux-sample")
include("webflux-sample:biz-webflux-sample")
include("webflux-sample:boot-webflux-sample")
