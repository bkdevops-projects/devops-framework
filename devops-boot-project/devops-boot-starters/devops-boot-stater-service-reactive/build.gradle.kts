description = "Starter for DevOps Boot Service Reactive"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-service-reactive"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-webflux"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-loadbalancer"))
}
