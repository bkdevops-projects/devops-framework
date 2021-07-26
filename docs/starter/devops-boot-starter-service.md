# devops-boot-starter-service

`starter-service`组件帮助开发者完成与微服务相关的快速配置

## 功能介绍
- 引入`spring-cloud-starter-openfeign`
- 引入`okhttp`并配置feign底层使用`OkHttpClient`
- 支持服务前缀/后缀的配置
- 配置consul服务注册instanceId
- 配置consul服务注册健康检查
- 配置consul配置中心规则
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

**说明：** 在gradle编译打包时，默认会将consul相关的微服务组件打包，所以不需要声明对consul的依赖，详情见[k8s云原生编译](/k8s/compile.md)

## 配置属性

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| service.prefix  | string | null  | 服务名称前缀，如prefix-，默认为空 |
| service.suffix  | string | null  | 服务名称后缀, 如-suffix，默认为空 |

## 详细介绍
### 1. 引入`spring-cloud-starter-openfeign`

引入`openfeign`并自动配置相关属性，如超时时间、请求压缩等，详见[common-service.properties](https://github.com/bkdevops-projects/devops-framework/blob/master/devops-boot-project/devops-boot-starters/devops-boot-starter-service/src/main/resources/common-service.properties)

### 2.引入`okhttp`并配置feign底层使用`OkHttpClient`

因为`okhttp`具有更优的性能，以及更优雅的api设计，所以引入`okhttp`并配置feign底层使用`OkHttpClient`

### 3. 支持服务前缀/后缀的配置

当多个应用部署在同一个服务注册中心时，可能会存在服务命名冲突，所以添加了对服务名称前缀/后缀的支持。
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
| consul服务实例instanceId  | prefix-test-8080-<ip-address> |
| consul配置中心key路径  | prefix-config/prefix/data |

### 4. 配置consul服务注册instanceId
为了保证consul注册实例唯一，自动配置instanceId包含主机的ip地址。完整的instanceId规则为`<prefix><app-name><suffix>-<port>-<ip-address>`。
如`xxx-test8080-192-168-1-1

### 5. 配置consul服务注册健康检查
健康检查地址`/actuator/health`, 检查间隔时间为10秒，超时时间为5秒

### 6. 配置consul配置中心规则
为了保证多个应用在同一个consul中使用配置中心不产生冲突，规定了配置路径，规则如下：
- 微服务对应的配置key`<prefix>config<suffix>/<app-name>/data`
- 配置格式为`YAML`
- profile分隔符为`::`
