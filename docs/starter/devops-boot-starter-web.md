# devops-boot-starter-web

`starter-web`组件帮助开发者完成与web相关的快速配置

## 功能介绍
- 引入`undertow`
- 引入`spring-boot`的`actuator`组件
- 引入`springfox-starter`并完成`swagger-ui`的自动化配置 **(OpenApi3.0规范)**
- 引入`jackson`并完成序列化和反序列化的配置
- 自定义`devops-boot`的启动banner
- 提供常用的工具类和方法


## 使用方式
- **build.gradle.kts**

```kotlin
implementation("com.tencent.devops:devops-boot-starter-web")
```

- **build.gradle**

```groovy
implementation 'com.tencent.devops:devops-boot-starter-web'
```

## 模块依赖
`starter-web`添加了如下依赖，对于这些模块的依赖不需要重复声明:
- `com.tencent.devops:devops-boot-starter-api`
- `com.tencent.devops:devops-boot-starter-logging`

## 配置属性

swagger配置过程中会读取以下配置

  | 属性               | 类型    | 默认值 | 说明               |
    | ------------------ | ------- | ------ | ------------------ |
  | spring.application.name  | string | null  | 应用名称，swagger会页面展示该值 |
  | spring.application.desc  | string | null  | 应用描述，swagger会页面展示该值 |
  | spring.application.version  | string | null  | 应用版本，swagger会页面展示该值 |

## 参考

- `springfox starter`[文档地址](http://springfox.github.io/springfox/docs/current/)
