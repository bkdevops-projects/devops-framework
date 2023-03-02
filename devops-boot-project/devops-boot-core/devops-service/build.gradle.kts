description = "DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-web"))
    api("com.squareup.okhttp3:okhttp")
    api("org.springframework.cloud:spring-cloud-commons")
    api("org.springframework.cloud:spring-cloud-starter-bootstrap")
}
