plugins {
    id("java-gradle-plugin")
}

description = "DevOps Publish Gradle Plugin"

dependencies {
    implementation(project(":devops-boot-project:devops-boot-tools:devops-gradle-plugin-common"))
    implementation("org.semver:api:0.9.33")
    implementation("org.apache.maven.scm:maven-scm-api:2.0.1")
    implementation("org.apache.maven.scm:maven-scm-provider-gitexe:2.0.1")
}

gradlePlugin {
    plugins {
        create("DevOpsReleasePlugin") {
            id = "com.tencent.devops.release"
            displayName = "DevOps Release Gradle Plugin"
            description = "DevOps Release Gradle Plugin"
            implementationClass = "com.tencent.devops.DevOpsReleasePlugin"
        }
    }
}
