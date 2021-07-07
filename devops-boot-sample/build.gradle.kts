plugins {
    id("com.tencent.devops.boot") version "0.0.4-SNAPSHOT"
}

allprojects {
    group="com.tencent.devops.sample"
    version="1.0.0-SNAPSHOT"

    apply(plugin = "com.tencent.devops.boot")

    // for debug locally
    repositories {
        mavenLocal()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES)
    }
}
