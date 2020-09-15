pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.spring.io/plugins-release")
        }
    }
}

rootProject.name = "devops-framework"

fun File.directories() = listFiles()?.filter { it.isDirectory && it.name != "build" }?.toList() ?: emptyList()

include("devops-boot-project:devops-boot")
include("devops-boot-project:devops-boot-dependencies")
include("devops-boot-project:devops-boot-starters")
include("devops-boot-project:devops-boot-tools:devops-boot-gradle-plugin")
include("devops-boot-example")

file("$rootDir/devops-boot-project/devops-boot").directories().forEach {
    include("devops-boot-project:devops-boot:${it.name}")
}

file("$rootDir/devops-boot-project/devops-boot-starters").directories().forEach {
    include("devops-boot-project:devops-boot-starters:${it.name}")
}
