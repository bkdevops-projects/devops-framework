plugins {
    `java-platform`
    id("publish")
}

description = "DevOps Boot Dependencies"

javaPlatform {
    allowDependencies()
}

dependencies {
    // Spring
    api(platform(MavenBom.SpringBoot))
    api(platform(MavenBom.SpringCloud))

    constraints {
        rootProject.subprojects.filter { it.name != project.name }.forEach { api(project(it.path)) }

        // jwt
        api("io.jsonwebtoken:jjwt-api:0.11.2")
        api("io.jsonwebtoken:jjwt-impl:0.11.2")
        api("io.jsonwebtoken:jjwt-jackson:0.11.2")

        // apache common
        api("commons-io:commons-io:2.6")
        api("org.apache.commons:commons-compress:1.18")
        api("org.apache.hadoop:hadoop-hdfs:2.6.0")
        api("org.apache.hadoop:hadoop-common:2.6.0")

        // guava
        api("com.google.guava:guava:29.0-jre")

        // swagger
        api("io.swagger:swagger-annotations:1.5.20")
        api("io.swagger:swagger-models:1.5.20")
        api("io.springfox:springfox-boot-starter:3.0.0")

        // mq stream
        api("org.apache.pulsar:pulsar-client:2.9.1")
        api("com.google.protobuf:protobuf-java:3.19.4")

        // s3
        api("com.amazonaws:aws-java-sdk-s3:1.11.700")

        // crypto
        api("com.tencent.bk.sdk:crypto-java-sdk:1.1.1")
    }
}
