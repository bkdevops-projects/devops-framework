# devops-boot-starter-service

`starter-service`组件帮助开发者完成与微服务相关的快速配置

## 功能介绍
- 引入`spring-cloud-starter-openfeign`
- 引入`spring-cloud-starter-consul-discovery`
- 引入`spring-cloud-starter-consul-config`
- 引入`okhttp`并配置feign底层使用`OkHttpClient`
- 引入`jackson`并完成序列化和反序列化的配置
- 配置自定义`RequestMappingHandlerMapping`解决`SpringMVC`注解重复扫描问题


## 使用方式
- **build.gradle.kts**

```kotlin
implementation("com.tencent.devops:devops-boot-starter-service")
```

- **build.gradle**

```groovy
implementation 'com.tencent.devops:devops-boot-starter-service'
```

## 模块依赖
`starter-serivce`添加了如下依赖，对于这些模块的依赖不需要重复声明:
- `com.tencent.devops:devops-boot-starter-web`
