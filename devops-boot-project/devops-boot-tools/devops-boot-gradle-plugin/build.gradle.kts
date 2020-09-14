plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    id("java-gradle-plugin")
}

description = "DevOps Boot Gradle Plugin"

dependencies {
    // 插件中引用kotlin gradle相关api
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
}

gradlePlugin {
    plugins {
        create("DevOpsBootPlugin") {
            id = "devops-boot-gradle-plugin"
            implementationClass = "com.tencent.devops.DevOpsBootPlugin"
        }
    }
}

pluginBundle {
    website = "http://www.gradle.org/"
    vcsUrl = "https://github.com/gradle/gradle"
    description = "DevOps greeting plugin!"

    (plugins) {
        "DevOpsBootPlugin" {
            tags = listOf("devops", "boot")
            version = "0.0.1"
        }
    }
}