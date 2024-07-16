description = "DevOps Boot Schedule Worker"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-schedule:devops-schedule-common"))
    api(project(":devops-boot-project:devops-boot-core:devops-common"))
    api("io.kubernetes:client-java")
    compileOnly("org.springframework.cloud:spring-cloud-starter")
}
