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

        // s3
        api("com.amazonaws:aws-java-sdk-s3:1.11.700")
    }

    //Spring
    api(platform(MavenBom.SpringBoot))
    api(platform(MavenBom.SpringCloud))

    // TODO : it can be deleted after Spring cloud upgrade feign version (>= 11.4)
    api(enforcedPlatform("io.github.openfeign:feign-core:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-jackson:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-jaxrs:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-okhttp:11.6"))

    //TODO : it can be deleted after Spring cloud upgrade log4j version (>= 2.15.0)
    api(enforcedPlatform("org.apache.logging.log4j:log4j-api:2.15.0"))
    api(enforcedPlatform("org.apache.logging.log4j:log4j-core:2.15.0"))
}
