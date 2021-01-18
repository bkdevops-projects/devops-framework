plugins {
    id("com.tencent.devops.boot") version "0.0.4-SNAPSHOT"
}

group="com.tencent.devops.sample"
version="1.0.0-SNAPSHOT"

allprojects {
    apply(plugin = "com.tencent.devops.boot")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES)
    }
}
