package publish

apply(plugin = "signing")
apply(plugin = "maven-publish")

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

configure<PublishingExtension> {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            name = "center"
            url = if (isReleaseVersion) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name.set(project.name)
            description.set(project.description)
            url.set("https://github.com/bkdevops-projects/devops-framework")
            licenses {
                license {
                    name.set("The MIT License (MIT)")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    name.set("carrypan")
                    email.set("carrypan@tencent.com")
                    url.set("https://github.com/carrypann")
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

    configure<SigningExtension> {
        val signingKeyId: String? by project
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys( signingKeyId, signingKey, signingPassword)
        setRequired({ isReleaseVersion && gradle.taskGraph.hasTask("upload")})
        sign(publications)
    }
}

extensions.findByType(JavaPluginExtension::class.java)?.run {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    withType<Jar> {
        manifest {
            attributes("Implementation-Title" to (project.description ?: project.name))
            attributes("Implementation-Version" to Release.Version)
        }
    }
}

