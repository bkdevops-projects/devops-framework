

description = "Tencent BlueKing DevOps Framework Build"

plugins {
    kotlin("jvm") version Versions.Kotlin apply false
    kotlin("kapt") version Versions.Kotlin apply false
    kotlin("plugin.spring") version Versions.Kotlin apply false
    id("io.spring.dependency-management") version Versions.DependencyManagement apply false
    id("com.tencent.devops.release") version Versions.DevopsReleasePlugin
    id("com.tencent.devops.publish") version Versions.DevopsReleasePlugin
}

val projectVersion = rootProject.file("version.txt").readText().trim()

allprojects {
    group = "com.tencent.devops"
    version = projectVersion

    apply(plugin = "com.tencent.devops.publish")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    publishing {
        publications {
            withType<MavenPublication> {
                pom {
                    name.set(project.name)
                    description.set(project.description ?: project.name)
                    url.set("https://github.com/bkdevops-projects/devops-framework")
                    licenses {
                        license {
                            name.set("The MIT License (MIT)")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    organization {
                        name.set("Tencent BK-CI")
                        url.set("https://github.com/Tencent/bk-ci")
                    }
                    developers {
                        developer {
                            name.set("blueking")
                            email.set("contactus_bk@tencent.com")
                            url.set("https://github.com/TencentBlueKing")
                            roles.set(listOf("Java Developer"))
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/bkdevops-projects/devops-framework.git")
                        developerConnection.set("scm:git:ssh://github.com/bkdevops-projects/devops-framework.git")
                        url.set("https://github.com/bkdevops-projects/devops-framework")
                    }
                }

            }
        }
    }
}

subprojects {
    apply(plugin = "ktlint")
}

release {
    scmUrl.set("scm:git:https://github.com/bkdevops-projects/devops-framework.git")
    incrementPolicy.set("PATCH")
}
