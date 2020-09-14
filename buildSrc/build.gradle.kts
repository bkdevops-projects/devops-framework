plugins {
    kotlin("jvm") version "1.3.72"
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    test {
        useJUnitPlatform()
    }
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
}

gradlePlugin {
    plugins {

    }
}
