rootProject.name = "devops-boot-sample"

pluginManagement {
    val devopsBootVersion: String by settings
    plugins {
        id("com.tencent.devops.boot") version devopsBootVersion
    }
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("boot-kotlin-sample")
include("boot-java-sample")
