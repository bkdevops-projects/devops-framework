description = "DevOps Boot Dependencies"

dependencyManagement {
    dependencies {
        dependency("com.tencent.devops:demo:${Release.Version}")
        dependency("com.tencent.devops:devops-boot-starter-demo:${Release.Version}")
    }
}