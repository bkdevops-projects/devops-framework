rootProject.name = "devops-framework"

fun File.directories() = listFiles()?.filter { it.isDirectory && it.name != "build" }?.toList() ?: emptyList()

fun includeAll(module: String) {
    include(module)
    val name = module.replace(":", "/")
    file("$rootDir/$name/").directories().forEach {
        include("$module:${it.name}")
    }
}

includeAll("devops-boot-project:devops-boot-core")
includeAll("devops-boot-project:devops-boot-dependencies")
includeAll("devops-boot-project:devops-boot-starters")
includeAll("devops-boot-project:devops-boot-tools")

includeAll("devops-boot-project:devops-boot-core:devops-plugin")
