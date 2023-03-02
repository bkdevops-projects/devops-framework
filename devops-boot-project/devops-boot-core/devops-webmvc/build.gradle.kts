description = "DevOps Boot Web"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-web"))
    api("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    api("org.springframework.boot:spring-boot-starter-undertow")
}
