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
    apply(plugin = "publish")

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
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-java-parameters")
            kotlinOptions.jvmTarget = Versions.Java
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = Versions.Java
        }
        test {
            useJUnitPlatform()
        }
        withType(org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask::class.java).configureEach {
            kotlinOptions.jvmTarget = Versions.Java
        }
    }
}
