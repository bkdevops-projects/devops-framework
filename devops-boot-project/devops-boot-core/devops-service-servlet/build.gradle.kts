description = "DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-service"))
    api(project(":devops-boot-project:devops-boot-core:devops-webmvc"))
    api("io.github.openfeign:feign-okhttp")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
}
