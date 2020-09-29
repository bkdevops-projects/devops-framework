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

include("springboot-kotlin-sample")
include("springboot-java-sample")
