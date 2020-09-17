plugins {
    id("devops-boot-gradle-plugin")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.tencent.devops:devops-boot-starter-demo")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}