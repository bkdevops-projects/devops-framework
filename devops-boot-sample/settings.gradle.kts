rootProject.name = "devops-boot-sample"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("springboot-kotlin-sample")
include("springboot-java-sample")
include("springboot-mixed-sample")