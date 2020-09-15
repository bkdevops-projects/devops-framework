plugins {
    id("com.gradle.plugin-publish") version Versions.PluginPublish
    id("java-gradle-plugin")
}

description = "DevOps Boot Gradle Plugin"

dependencies {
    // 插件中引用kotlin gradle相关api
    api("org.jetbrains.kotlin:kotlin-gradle-plugin")
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
    description = "DevOps boot plugin"

    (plugins) {
        "DevOpsBootPlugin" {
            tags = listOf("devops", "boot")
            version = "0.0.1"
        }
    }
}