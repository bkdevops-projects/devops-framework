package publish

apply(plugin = "publish.base")

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("pom") {
            from(components["javaPlatform"])
        }
    }
}
