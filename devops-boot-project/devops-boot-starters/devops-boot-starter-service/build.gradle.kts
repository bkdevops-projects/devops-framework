description = "Starter for DevOps Boot Service"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-service"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-web"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-loadbalancer"))
    api(project(":devops-boot-project:devops-boot-starters:devops-boot-starter-circuitbreaker"))
}
