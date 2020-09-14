plugins {
    kotlin("jvm")
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
//    id("devops-boot-gradle-plugin") version "0.0.1-SNAPSHOT"
}

group="com.tencent.devops.example"
version="1.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("com.tencent.devops:devops-boot-dependencies:0.0.1-SNAPSHOT")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.tencent.devops:devops-boot-starter-demo:0.0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "minutes")
}