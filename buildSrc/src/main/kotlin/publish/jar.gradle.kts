package publish

apply(plugin = "publish.base")

configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("jar") {
            from(components["java"])
            pom {
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                }
                scm {
                    connection.set("scm:git:git://github.com/bkdevops-projects/devops-framework.git")
                    developerConnection.set("scm:git:ssh://github.com/bkdevops-projects/devops-framework.git")
                    url.set("https://github.com/bkdevops-projects/devops-framework")
                }
            }
        }
    }

    configure<SigningExtension> {
        val signingKeyId: String? by project
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        setRequired({ gradle.taskGraph.hasTask("publish") })
        sign(publications["jar"])
    }
}
