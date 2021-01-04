description = "DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-web"))
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("com.squareup.okhttp3:okhttp")
    api("io.github.openfeign:feign-okhttp")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    api("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    api("org.springframework.cloud:spring-cloud-starter-consul-config")
    api("org.springframework.cloud:spring-cloud-starter-netflix-hystrix")
}
