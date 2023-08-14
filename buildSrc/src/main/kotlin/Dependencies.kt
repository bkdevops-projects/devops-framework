object Libs {
    const val KotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin}"
    const val KotlinReflectLib = "org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}"
    const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
    const val SpringBootGradlePlugin = "org.springframework.boot:spring-boot-gradle-plugin:${Versions.SpringBoot}"
    const val DependencyManagement = "io.spring.gradle:dependency-management-plugin:${Versions.DependencyManagement}"
    const val KotlinSpringGradlePlugin = "org.jetbrains.kotlin:kotlin-allopen:${Versions.Kotlin}"
    const val KtLint = "com.pinterest:ktlint:${Versions.KtLint}"
    const val GoogleJibPlugin = "gradle.plugin.com.google.cloud.tools:jib-gradle-plugin:${Versions.Jib}"
    const val GradleNexuxPublishPlugin = "io.github.gradle-nexus:publish-plugin:${Versions.GradleNexusPublish}"
}

object MavenBom {
    const val SpringBoot = "org.springframework.boot:spring-boot-dependencies:${Versions.SpringBoot}"
    const val SpringCloud = "org.springframework.cloud:spring-cloud-dependencies:${Versions.SpringCloud}"
    const val DevOpsBoot = ":devops-boot-project:devops-boot-dependencies"
}

