plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
}

description = "Starter for DevOps Boot"

subprojects {
    apply(plugin = "java-library")
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
    }
}
