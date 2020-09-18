rootProject.name = "devops-framework"

fun File.directories() = listFiles()?.filter { it.isDirectory && it.name != "build" }?.toList() ?: emptyList()

include("devops-boot-project:devops-boot-core")
include("devops-boot-project:devops-boot-dependencies")
include("devops-boot-project:devops-boot-starters")
include("devops-boot-project:devops-boot-tools:devops-boot-gradle-plugin")

file("$rootDir/devops-boot-project/devops-boot-core").directories().forEach {
    include("devops-boot-project:devops-boot-core:${it.name}")
}

file("$rootDir/devops-boot-project/devops-boot-starters").directories().forEach {
    include("devops-boot-project:devops-boot-starters:${it.name}")
}
