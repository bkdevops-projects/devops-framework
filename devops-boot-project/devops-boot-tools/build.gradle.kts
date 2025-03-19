import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "Tools for DevOps Boot"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks {
        getByName<KotlinCompile>("compileKotlin") {
            kotlinOptions.jvmTarget = Versions.Java
        }
    }
}
