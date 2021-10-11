import com.github.gradle.node.npm.task.NpmTask

description = "DevOps Boot Schedule Server"

plugins {
    id("com.github.node-gradle.node") version "3.0.1"
}

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-schedule:devops-schedule-common"))
    api(project(":devops-boot-project:devops-boot-core:devops-schedule:devops-schedule-model"))
    api("io.jsonwebtoken:jjwt-api")
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    compileOnly("org.springframework.cloud:spring-cloud-starter")
}

sourceSets {
    main {
        resources {
            // 只打包dist目录，加快build，减小打包体积
            include("frontend/dist/**")
            include("META-INF/**")
        }
    }
}

node {
    download.set(false)
    nodeProjectDir.set(project.projectDir.resolve("src/main/resources/frontend"))
}

tasks.register<NpmTask>("buildFrontend") {
    dependsOn("npm_install")
    args.set(listOf("run", "build"))
}

tasks.getByName("build").dependsOn("buildFrontend")
