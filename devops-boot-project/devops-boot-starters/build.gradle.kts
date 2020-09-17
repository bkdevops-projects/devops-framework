plugins {
    kotlin("jvm") version Versions.Kotlin
    id("io.spring.dependency-management") version Versions.DependencyManagement
}

description = "Starter for DevOps Boot"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "publish.jar")

    dependencyManagement {
        imports {
            mavenBom(MavenBom.SpringBoot)
            mavenBom(MavenBom.SpringCloud)
        }
    }
}
