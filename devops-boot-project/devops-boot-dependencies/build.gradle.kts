plugins {
    `java-platform`
    id("publish.pom")
}

description = "DevOps Boot Dependencies"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        rootProject.subprojects.filter { it.name != project.name }.forEach { api(project(it.path)) }
    }
    api(platform(MavenBom.SpringBoot))
    api(platform(MavenBom.SpringCloud))
}
