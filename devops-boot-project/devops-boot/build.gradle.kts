description = "DevOps Boot"

subprojects {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
    }
}
