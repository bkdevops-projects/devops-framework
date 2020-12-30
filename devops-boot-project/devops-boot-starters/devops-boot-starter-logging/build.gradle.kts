description = "Starter for DevOps Boot Logging"

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    api(project(":devops-boot-project:devops-boot-core:devops-logging"))
}
