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
    }

    //Spring
    api(platform(MavenBom.SpringBoot))
    api(platform(MavenBom.SpringCloud))

    // jwt
    api(enforcedPlatform("io.jsonwebtoken:jjwt-api:0.11.2"))
    api(enforcedPlatform("io.jsonwebtoken:jjwt-impl:0.11.2"))
    api(enforcedPlatform("io.jsonwebtoken:jjwt-jackson:0.11.2"))

    // apache common
    api(enforcedPlatform("commons-io:commons-io:2.6"))
    api(enforcedPlatform("org.apache.commons:commons-compress:1.18"))
    api(enforcedPlatform("org.apache.hadoop:hadoop-hdfs:2.6.0"))
    api(enforcedPlatform("org.apache.hadoop:hadoop-common:2.6.0"))

    // guava
    api(enforcedPlatform("com.google.guava:guava:29.0-jre"))

    // swagger
    api(enforcedPlatform("io.swagger:swagger-annotations:1.5.20"))
    api(enforcedPlatform("io.swagger:swagger-models:1.5.20"))
    api(enforcedPlatform("io.springfox:springfox-boot-starter:3.0.0"))

    // s3
    api(enforcedPlatform("com.amazonaws:aws-java-sdk-s3:1.11.700"))

    // TODO : it can be deleted after Spring cloud upgrade feign version (>= 11.4)
    api(enforcedPlatform("io.github.openfeign:feign-core:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-jackson:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-jaxrs:11.6"))
    api(enforcedPlatform("io.github.openfeign:feign-okhttp:11.6"))
}
