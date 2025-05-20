import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

description = "DevOps Boot"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom(MavenBom.SpringBoot)
            mavenBom(MavenBom.SpringCloud)
        }
        pomCustomizationSettings.isEnabled = false
    }

    dependencies {
        implementation(platform(project(MavenBom.DevOpsBoot)))
        implementation(Libs.KotlinReflectLib)
        implementation(Libs.KotlinStdLib)
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks {
        compileJava {
            sourceCompatibility = Versions.Java
            targetCompatibility = Versions.Java
        }
        compileKotlin {
            compilerOptions {
                freeCompilerArgs.add("-Xjsr305=strict")
                freeCompilerArgs.add("-java-parameters")
                jvmTarget.set(JvmTarget.fromTarget(Versions.Java))
            }
        }
        compileTestKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(Versions.Java))
            }
        }
        test {
            useJUnitPlatform()
        }
        withType(org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask::class.java).configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(Versions.Java))
            }
        }
    }
}
