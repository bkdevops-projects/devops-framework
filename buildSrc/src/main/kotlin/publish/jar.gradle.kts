package publish

apply(plugin = "publish.base")

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("jar") {
            from(components["java"])
        }
    }
}
