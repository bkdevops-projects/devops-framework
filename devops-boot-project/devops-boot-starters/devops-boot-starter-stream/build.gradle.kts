description = "Stream Starter for DevOps Boot"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-pulsar"))
    api("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    api("org.springframework.cloud:spring-cloud-starter-stream-rabbit")
}
