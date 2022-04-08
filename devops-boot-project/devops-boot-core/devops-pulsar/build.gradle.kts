description = "DevOps Boot Pulsar"

dependencies {
    api("org.springframework.cloud:spring-cloud-stream:3.0.11.RELEASE")
    api("org.springframework.boot:spring-boot-actuator")
    api("org.springframework.boot:spring-boot-actuator-autoconfigure")
    api("org.apache.pulsar:pulsar-client:2.8.1")
    api("com.google.protobuf:protobuf-java:3.19.4")
}
