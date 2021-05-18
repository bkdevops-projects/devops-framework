description = "DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-web"))
    api("com.squareup.okhttp3:okhttp")
    api("io.github.openfeign:feign-okhttp")
    api("org.springframework.cloud:spring-cloud-commons")
}
