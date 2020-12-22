plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("publish")
}

description = "DevOps Boot Gradle Plugin"

dependencies {
    implementation(Libs.KotlinGradlePlugin)
    implementation(Libs.SpringBootGradlePlugin)
    implementation(Libs.DependencyManagement)
    implementation(Libs.KotlinSpringGradlePlugin)
    implementation(Libs.KtLint)
}

gradlePlugin {
    plugins {
        create("DevOpsBootPlugin") {
            id = "com.tencent.devops.boot"
            displayName = "DevOps Boot Gradle Plugin"
            description = "DevOps Boot Gradle Plugin"
            implementationClass = "com.tencent.devops.DevOpsBootPlugin"
        }
    }
}
