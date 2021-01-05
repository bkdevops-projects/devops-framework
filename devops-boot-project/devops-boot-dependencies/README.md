# devops-boot-dependencies

## 功能介绍

`devops-boot-dependencies`用于提供统一的版本依赖管理能力，在`Spring Boot`以及`Spring Cloud`的`bom`基础上，添加了`devops-boot`的版本管理。


## 使用方式
### 1. 配合devops-boot插件使用(推荐)
当项目引入了`devops-boot-gradle-plugin`插件，会自动帮我们配置`devops-boot-dependencies`，无需任何配置

### 2. 独立使用

- **build.gradle.kts**

```kotlin
dependencyManagement {
    imports {
        mavenBom("com.tencent.devops:devops-boot-dependencies:${version}")
    }
}
```

- **build.gradle**

```groovy
dependencyManagement {
     imports {
          mavenBom 'com.tencent.devops:devops-boot-dependencies:${version}'
     }
}
```

## 依赖版本列表
请参考[build.gradle.kts](./build.gradle.kts)的`constraints`列表

### 参考
关于依赖管理的使用详情，请参考官方文档[dependency-management](https://docs.spring.io/dependency-management-plugin/docs/current/reference/html/)



