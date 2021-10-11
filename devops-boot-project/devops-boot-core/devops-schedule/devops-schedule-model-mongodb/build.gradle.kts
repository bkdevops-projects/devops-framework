description = "DevOps Boot Schedule Model MongoDB"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-schedule:devops-schedule-model"))
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
}
