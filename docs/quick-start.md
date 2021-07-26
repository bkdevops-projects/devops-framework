# 快速开始

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

- 编写启动类 **TestApplication.kt**
```kotlin
@RestController
@SpringBootApplication
class TestApplication {

    @GetMapping
    fun greeting(): String = "Hello, Devops-Boot!"
}

fun main(args: Array<String>) {
    runApplication<TestApplication>(args)
}
```

- 启动，Enjoy it!


?> 如上，我们只添加了`devops-boot-gradle`插件，就能进行SringBoot甚至SpringCloud应用的开发了，无需额外操作，自动为我们配置好`jdk`版本、编译选项、依赖管理、`kotlin`依赖及`kotlin-spring`插件等繁琐的配置项。

接下来即可直接开始业务逻辑代码的编写了。
