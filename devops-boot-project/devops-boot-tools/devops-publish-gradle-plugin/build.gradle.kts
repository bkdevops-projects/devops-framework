plugins {
    id("java-gradle-plugin")
}

description = "DevOps Publish Gradle Plugin"

dependencies {
    implementation(project(":devops-boot-project:devops-boot-tools:devops-gradle-plugin-common"))
    implementation(Libs.KotlinGradlePlugin)
    implementation(Libs.DependencyManagement)
    implementation(Libs.GradleNexuxPublishPlugin)
}

gradlePlugin {
    plugins {
        create("DevOpsPublishPlugin") {
            id = "com.tencent.devops.publish"
            displayName = "DevOps Publish Gradle Plugin"
            description = "DevOps Publish Gradle Plugin"
            implementationClass = "com.tencent.devops.DevOpsPublishPlugin"
        }
    }
}
