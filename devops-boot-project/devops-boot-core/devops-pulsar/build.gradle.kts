description = "DevOps Boot Pulsar"

dependencies {
    api("org.springframework.cloud:spring-cloud-stream")
    api("org.springframework.boot:spring-boot-actuator")
    api("org.springframework.boot:spring-boot-actuator-autoconfigure")
    api("org.apache.pulsar:pulsar-client")
    api("com.google.protobuf:protobuf-java")
}
