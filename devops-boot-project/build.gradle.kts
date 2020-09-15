plugins {
    kotlin("jvm") version Versions.Kotlin
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement
}

description = "Tencent BlueKing DevOps Framework Build"

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(from = rootProject.file("gradle/publish.gradle.kts"))

    dependencyManagement {
        imports {
            mavenBom(MavenBom.SpringBoot)
            mavenBom(MavenBom.SpringCloud)
        }
        dependencies {
            dependency("com.tencent.devops:demo:${Release.Version}")
            dependency("com.tencent.devops:devops-boot-starter-demo:${Release.Version}")
        }
    }

    dependencies {
        implementation(Libs.KotlinStdLib)
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
                attributes("Implementation-Version" to Release.Version)
            }
        }
    }

}
