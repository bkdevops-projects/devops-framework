# devops-boot-starter-api

`starter-api`组件用于帮助开发者完成api接口的编写

## 功能介绍
- 规范统一的接口协议并定义统一的`POJO`类
- 自动引入`swagger`注解需要的相关jar包
- 提供`HTTP`相关的常用常量和枚举类

## 使用方式
- **build.gradle.kts**

```kotlin
implementation("com.tencent.devops:devops-boot-starter-api")
```

- **build.gradle**

```groovy
implementation 'com.tencent.devops:devops-boot-starter-api'
```

## 最佳实践

项目的`api-xxx`模块包含了对外接口的声明以及数据格式的定义，最终作为二方包或者三方包提供给对外使用，因此需要开发者保持`api`模块的精简和规范，否则会引诸多问题，如：
1. 不规范的依赖声明方式导致版本依赖冲突
2. 引入过多&多余的依赖，导致模块臃肿
3. 在`api`模块中暴露敏感信息，如`DO`类

`starter-api`组件秉承这一原则，帮助开发者最大化精简和规范`api`模块的开发，但开发者仍然需要注意以下事项：
1. `api`模块慎重引入依赖，如需引入请使用`implementation`或`comipleOnly`方式
2. 对于`@RequestMapping`、`@FeignClient`注解的依赖，使用`comipleOnly`的方式依赖
    ```kotlin
      compileOnly("org.springframework.cloud:spring-cloud-openfeign-core")
    ```
3. `api`模块保持简洁，只包含和接口声明和数据格式的定义
