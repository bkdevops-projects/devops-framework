plugins {
    kotlin("jvm") version Versions.Kotlin
    kotlin("kapt") version Versions.Kotlin
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement
}

description = "DevOps Boot"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "publish.jar")

    dependencyManagement {
        imports {
            mavenBom(MavenBom.SpringBoot)
            mavenBom(MavenBom.SpringCloud)
        }
    }

    dependencies {
        implementation(Libs.KotlinStdLib)
        implementation("org.springframework.boot:spring-boot-starter")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
    }

    tasks {
        compileKotlin {
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            kotlinOptions.jvmTarget = Versions.Java
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = Versions.Java
        }
        test {
            useJUnitPlatform()
        }
        jar {
            manifest {
                attributes("Implementation-Title" to (project.description ?: project.name))
                attributes("Implementation-Version" to Release.Version)
            }
        }
    }
}
