description = "Tencent BlueKing DevOps Framework Build"

plugins {
    kotlin("jvm") version Versions.Kotlin apply false
    kotlin("kapt") version Versions.Kotlin apply false
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement apply false
}

allprojects {
    group = Release.Group
    version = Release.Version

    repositories {
        mavenCentral()
        jcenter()
    }
}