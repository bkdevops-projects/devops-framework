rootProject.name = "devops-boot-sample"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("api-kotlin-sample")
include("biz-kotlin-sample")
include("boot-java-sample")
include("boot-kotlin-sample")

