description = "DevOps Boot Schedule Common"

dependencies {
    api(project(":devops-boot-project:devops-boot-core:devops-webmvc"))
    api(project(":devops-boot-project:devops-boot-core:devops-api"))
    api("com.squareup.okhttp3:okhttp")
}
