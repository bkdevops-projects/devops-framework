description = "DevOps Boot Web"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-boot"))
    api(project(":devops-boot-project:devops-boot-core:devops-logging"))
    api(project(":devops-boot-project:devops-boot-core:devops-utils"))
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui")
}
