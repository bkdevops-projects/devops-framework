package publish

apply(plugin = "signing")
apply(plugin = "maven-publish")

configure<PublishingExtension> {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            name = "center"
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}
tasks {
    withType<Jar> {
        manifest {
            attributes("Implementation-Title" to (project.description ?: project.name))
            attributes("Implementation-Version" to Release.Version)
        }
    }
}