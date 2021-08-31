plugins {
    kotlin("kapt")
}

dependencies {
    // 引入插件扩展点定义，实际开发中通过jar包引入
    implementation(project(":api-kotlin-sample"))
    kapt("com.tencent.devops:devops-plugin-processor")
}


val pluginId: String? by project
val pluginVersion: String? by project
val pluginScope: String? by project
val pluginAuthor: String? by project
val pluginDescription: String? by project

fun pluginId() = pluginId ?: name.removePrefix("plugin-")
fun pluginVersion() = pluginVersion ?: version
fun archiveFileName() = "plugin-${pluginId()}-${pluginVersion()}.jar"

tasks.withType<Jar> {
    archiveFileName.set(archiveFileName())
    manifest {
        attributes(
            "Plugin-Id" to pluginId(),
            "Plugin-Version" to pluginVersion(),
            "Plugin-Scope" to pluginScope,
            "Plugin-Author" to pluginAuthor,
            "Plugin-Description" to pluginDescription
        )
    }
}
