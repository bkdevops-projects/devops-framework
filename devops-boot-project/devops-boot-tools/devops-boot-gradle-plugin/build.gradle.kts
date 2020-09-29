plugins {
    kotlin("jvm")
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
//            id = "devops-boot-gradle-plugin"
            id = "com.tencent.devops.boot"
            implementationClass = "com.tencent.devops.DevOpsBootPlugin"
        }
    }
}
