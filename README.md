# devops-framework
![GitHub](https://img.shields.io/github/license/bkdevops-projects/devops-framework)
![Maven Central](https://img.shields.io/maven-central/v/com.tencent.devops/devops-boot)
![GitHub Workflow Status (event)](https://img.shields.io/github/workflow/status/bkdevops-projects/devops-framework/build)


`devops-framework`是一款基于`Spring Boot`的微服务快速开发框架，提炼自腾讯DevOps团队内部多个项目，使用约定优于配置的设计理念，帮助我们专注于`DevOps`业务快速开发。

## 项目特点
- 提供gradle快速开发插件[devops-boot-gradle-plugin](./devops-boot-project/devops-boot-tools/devops-boot-gradle-plugin/README.md)
- 提供gradle快速发布插件[devops-publish-gradle-plugin](./devops-boot-project/devops-boot-tools/devops-publish-gradle-plugin/README.md)
- 提供统一版本依赖管理[devops-boot-dependencies](./devops-boot-project/devops-boot-dependencies/README.md)
- 提供多个开箱即用的starter组件
  - [logging日志组件](./devops-boot-project/devops-boot-starters/devops-boot-starter-logging/README.md)
  - TODO

## 快速开始
- **gradle.build.kts**
```groovy
// 添加devops-boot gradle插件
plugins {
    id("com.tencent.devops.boot") version ${version}
}

dependencies {
    // 添加需要的starter组件
    implementation("com.tencent.devops:devops-boot-starter-web")
}
```
只需要添加一个`devops-boot`插件，会自动为我们配置`jdk`版本、编译选项、依赖管理、`kotlin`依赖及`kotlin-spring`插件等等繁琐的配置项。

接下来即可直接开始业务逻辑代码的编写了。


## 工程结构
```shell script
devops-framework/
├── buildSrc                      # gradle项目构建目录
├── devops-boot-project           # devops-boot源码目录
│   ├── devops-boot-core          # 核心模块
│   ├── devops-boot-dependencies  # maven bom模块
│   ├── devops-boot-starters      # starter组件目录
│   └── devops-boot-tools         # gradle脚本等工具目录
├── devops-boot-sample            # sample项目
└── docs                          # 开发文档
```

## 核心依赖

| 依赖          | 版本          |
| ------------ | ------------- |
| JDK          | 1.8+          |
| Kotlin       | 1.3.72        |
| Gradle       | 6.6.1         |
| Spring Boot  | 2.3.7.RELEASE |
| Spring Cloud | Hoxton.SR9    |


## 发行版本
- 0.0.1 2020年10月9日

