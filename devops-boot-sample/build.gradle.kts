group="com.tencent.devops.sample"
version="1.0.0-SNAPSHOT"

allprojects {

    repositories {
        mavenLocal()
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "minutes")
    }
}