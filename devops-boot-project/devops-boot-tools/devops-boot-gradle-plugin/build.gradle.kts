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
    implementation(Libs.KtLint) {
        // ktlint 引用了1.4.31版本，和项目引入的1.4.32有冲突
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
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
