description = "DevOps Boot Plugin Core"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-plugin:devops-plugin-api"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-webmvc")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
}
