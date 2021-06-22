# k8s云原生编译

## 原理

> 得益于SpringBoot的自动化装配原理以及SpringCloud组件的优秀封装，对于服务发现以及远程调用，开发者往往只需要关心上层接口，而底层则根据运行时的依赖包自动选择合适的实现方式。

近年来，Spring团队的重心逐渐趋向于云原生的支撑，SpringCloud发起了`SpringCloud Kubernetes`项目，帮助开发者进行云原生应用的开发。借助这个项目，我们可以直接将应用运行在Kubernetes环境中，并使用k8s的各个组件。

所以，如果能在项目编译编译/运行时选择`SpringCloud Kubernetes`依赖，我们就能实现同一套代码同时支持传统方式和云原生方式。

这里有两种思路，一是在运行时选择不同的底层依赖，二是在编译时选择不同依赖，而我们选择了后者。

### 运行时选择依赖 x

我们可以指定`org.springframework.boot.loader.PropertiesLauncher`作为启动类，在项目启动时设置外部classpath目录`-Dloader.path="/your/custom/classpath/"`，以此根据情况使用不同的依赖，如`spring-cloud-consul`、`kunernetes`、`nacos`、`eureka`

*这种方式虽然可行，但实际并不方便，增加了额外的部署成本，未来的版本框架升级也会带来冲突等问题。*


### **编译时选择依赖 √**

因为我们使用gradle作为构件工具，所以我们用一种动态化编译打包的思路来实现目的，

```kotlin
val assemblyMode: String? by project
val k8s: Boolean = assemblyMode?.equals("k8s") ?: false
dependencies {
    if (k8s) {
        api("org.springframework.cloud:spring-cloud-starter-kubernetes-all")
        api("org.springframework.cloud:spring-cloud-starter-kubernetes-ribbon")
    } else {
        api("org.springframework.cloud:spring-cloud-starter-consul-discovery")
        api("org.springframework.cloud:spring-cloud-starter-consul-config")
    }
}
```

在流水线上编译时，设置参数`assembly.mode=k8s`既可使用`SpringCloud Kubernetes`，否则使用`SpringCloud Consul`依赖。

## 使用方法

上面介绍了基本原理，而我们将具体实现封装在了`devops-boot-gradle-plugin`中，开发者只需在打包时根据需要传递配置。

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| assembly.mode  | String | null  | 打包类型，默认为consul。支持consul/k8s/kubernetes |

## 示例

### 默认方式(依赖SpringCloud Consul)
```
gradle build
```

### 指定consul方式打包(依赖SpringCloud Consul)
```
gradle build -Dassembly.mode=consul
```

### k8s打包(依赖SpringCloud Kubernetes)
> 一共有4种方式，优先级从上到下递减

```
// 方式1
gradle build -Dassembly.mode=k8s
// 方式2 设置系统环境变量 assembly.mode=k8s
gradle build
// 方式3
gradle build -Passembly.mode=k8s
// 方式4 修改配置文件gradle.properties assembly.mode=k8s
gradle build
```

## 参考

- [Spring Cloud Kubernetes](https://docs.spring.io/spring-cloud-kubernetes/docs/current/reference/html/)

