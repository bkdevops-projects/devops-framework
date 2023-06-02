plugins {
    kotlin("jvm")
    id("publish")
}

description = "DevOps Gradle Plugin Common"

dependencies {
    implementation(gradleApi())
}
