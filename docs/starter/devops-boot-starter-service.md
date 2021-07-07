# devops-boot-starter-service

`starter-service`组件帮助开发者完成与微服务相关的快速配置

## 功能介绍
- 引入`spring-cloud-starter-openfeign`
- 引入`okhttp`并配置feign底层使用`OkHttpClient`
- 配置服务前缀/后缀
- 配置consul服务注册instanceId
- 配置consul服务注册健康检查
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
- `com.tencent.devops:devops-boot-starter-loadbalancer`

## 配置属性

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| service.prefix  | string | null  | 服务名称前缀，如prefix-，默认为空 |
| service.suffix  | string | null  | 服务名称后缀, 如-suffix，默认为空 |

## 服务前后缀介绍
当多个应用部署在同一个服务注册中心时，可能会存在服务命名冲突，所以添加了对服务名称设置前缀/后缀。
默认情况下，前后缀都为空，此时的行为和SpringCloud默认行为一致。

假设一个应用有如下配置，

| 配置项             | 配置值    |
| ------------------ | ------- |
| spring.application.name  | test |
| service.prefix   | prefix-  |
| service.suffix   | null(空)  |
| server.port      | 8080 |

则相应的生效规则为，

| 配置项             | 配置值    |
| ------------------ | ------- |
| consul服务名称 | prefix-test |
| consul服务注册id  | prefix-test-8080-<hostname> |
| consul配置中心key路径  | prefix-config/prefix/data |
| 日志路径  | logs/test/test.log |


## consul组件依赖
在gradle编译打包时，默认会将consul相关的微服务组件打包，所以不需要声明对consul的依赖，详情见[k8s云原生编译](/k8s/compile.md)
