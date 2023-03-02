description = "DevOps Boot WebFlux"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-web"))
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}
