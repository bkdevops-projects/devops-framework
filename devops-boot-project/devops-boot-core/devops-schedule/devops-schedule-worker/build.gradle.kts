description = "DevOps Boot Schedule Worker"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-schedule:devops-schedule-common"))
    compileOnly("org.springframework.cloud:spring-cloud-starter")
}
