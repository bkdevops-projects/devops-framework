plugins {
    kotlin("jvm") version Versions.Kotlin
    id("java-gradle-plugin")
    id("publish.base")
}

description = "DevOps Boot Gradle Plugin"

dependencies {
    implementation(Libs.KotlinGradlePlugin)
    implementation(Libs.SpringBootGradlePlugin)
    implementation(Libs.DependencyManagement)
    implementation(Libs.KotlinSpringGradlePlugin)
}

gradlePlugin {
    plugins {
        create("DevOpsBootPlugin") {
            id = "devops-boot-gradle-plugin"
            implementationClass = "com.tencent.devops.DevOpsBootPlugin"
        }
    }
}
