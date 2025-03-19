plugins {
    `java-platform`
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
        api("io.jsonwebtoken:jjwt-api:0.12.6")
        api("io.jsonwebtoken:jjwt-impl:0.12.6")
        api("io.jsonwebtoken:jjwt-jackson:0.12.6")

        // apache common
        api("commons-io:commons-io:2.16.1")
        api("org.apache.commons:commons-compress:1.26.2")

        // apache hadoop
        api("org.apache.hadoop:hadoop-hdfs:3.4.0")
        api("org.apache.hadoop:hadoop-common:3.4.0")

        // guava
        api("com.google.guava:guava:33.2.1-jre")

        // swagger
        api("io.swagger.core.v3:swagger-annotations:2.2.22")
        api("io.swagger.core.v3:swagger-models:2.2.22")
        api("io.swagger.core.v3:swagger-jaxrs2:2.2.22")
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

        // mq stream
        api("org.apache.pulsar:pulsar-client:2.9.5")
        api("com.google.protobuf:protobuf-java:3.19.6")

        // s3
        api("com.amazonaws:aws-java-sdk-s3:1.11.700")

        // crypto
        api("com.tencent.bk.sdk:crypto-java-sdk:1.1.1")
    }
}
