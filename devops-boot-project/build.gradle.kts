plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring") apply false
    id("maven-publish")
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

description = "Tencent BlueKing DevOps Framework Build"

allprojects {
    group = "com.tencent.devops"
    version = "$version"

    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:2.3.3.RELEASE")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR8")
        }
        dependencies {

        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }

    tasks {
        compileKotlin {
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
        test {
            useJUnitPlatform()
        }
    }

    publishing {
        publications {
            create<MavenPublication>("DevOpsBoot") {
                from(components["java"])
                pom {
                    licenses {  }
                    developers {  }
                    scm {  }
                }
            }
        }
        repositories {

        }
    }
}
