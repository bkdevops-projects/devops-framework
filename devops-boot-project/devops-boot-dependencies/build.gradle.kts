plugins {
    `java-platform`
    id("publish")
}

description = "DevOps Boot Dependencies"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        rootProject.subprojects.filter { it.name != project.name }.forEach { api(project(it.path)) }

        // undertow
        // issue https://github.com/spring-projects/spring-boot/issues/16407
        // issue https://issues.redhat.com/browse/UNDERTOW-1743
        api("io.undertow:undertow-core:2.1.1.Final")
        api("io.undertow:undertow-servlet:2.1.1.Final")
        api("io.undertow:undertow-websockets-jsr:2.1.1.Final")

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
        api("io.swagger:swagger-annotations:1.5.22")
        api("io.swagger:swagger-models:1.5.22")
        api("io.springfox:springfox-swagger2:2.9.2")

        // s3
        api("com.amazonaws:aws-java-sdk-s3:1.11.700")
    }
    api(platform(MavenBom.SpringBoot))
    api(platform(MavenBom.SpringCloud))
}
