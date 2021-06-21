# 快速开始
?> 待完善

- **gradle.build.kts**
    ```kotlin
    // 添加devops-boot gradle插件
    plugins {
        id("com.tencent.devops.boot") version ${version}
    }
    
    dependencies {
        // 添加需要的starter组件
        implementation("com.tencent.devops:devops-boot-starter-web")
    }
    ```

只需要添加`devops-boot`插件，就自动为我们配置好`jdk`版本、编译选项、依赖管理、`kotlin`依赖及`kotlin-spring`插件等等繁琐的配置项。

接下来即可直接开始业务逻辑代码的编写了。
