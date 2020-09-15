apply(plugin = "devops-boot-gradle-plugin")

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        dependencies {
            classpath("com.tencent.devops:devops-boot-gradle-plugin:0.0.1-SNAPSHOT")
        }
    }
}

group="com.tencent.devops.example"
version="1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    val implementation by configurations
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.tencent.devops:devops-boot-starter-demo:${Release.Version}")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}