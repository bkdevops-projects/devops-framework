plugins {
    id("com.tencent.devops.boot") version File("../version.txt").readText().trim()
}

allprojects {
    group="com.tencent.devops.sample"
    version="1.0.0-SNAPSHOT"

    // for debug devops-boot locally
    repositories {
        mavenLocal()
    }

    apply(plugin = "com.tencent.devops.boot")

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES)
    }
}
