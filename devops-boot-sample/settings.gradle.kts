rootProject.name = "devops-boot-sample"

// for debug devops-boot locally
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

include("api-kotlin-sample")
include("biz-kotlin-sample")
include("boot-java-sample")
include("boot-kotlin-sample")
include("plugin-printer")
include("boot-schedule-server-sample")
include("boot-schedule-worker-cloud-sample")
