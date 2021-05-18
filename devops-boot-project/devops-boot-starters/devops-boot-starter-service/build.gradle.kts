description = "Starter for DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-service"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-web"))
    api("org.springframework.cloud:spring-cloud-starter-bootstrap")
}
