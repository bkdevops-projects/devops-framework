<h1 align="center" style="font-weight: bold;">DevOps Boot</h1>
<h4 align="center">基于Spring Boot的微服务快速开发框架</h4>
<div align="center">

[![GitHub](https://img.shields.io/github/license/bkdevops-projects/devops-framework)](https://img.shields.io/github/license/bkdevops-projects/devops-framework)
[![Maven Central](https://img.shields.io/maven-central/v/com.tencent.devops/devops-boot)](https://img.shields.io/maven-central/v/com.tencent.devops/devops-boot)
[![GitHub Workflow Status (event)](https://img.shields.io/github/workflow/status/bkdevops-projects/devops-framework/build)](https://img.shields.io/github/workflow/status/bkdevops-projects/devops-framework/build)

</div>

----------

## DevOps Boot 是什么？

`devops-boot`提炼自腾讯DevOps团队内部多个项目，使用约定优于配置的设计理念，帮助我们专注于DevOps业务快速开发，它具有以下优势:

- **简单** ：几乎零配置快速开发微服务，低成本上手
- **易用** ：采用`Spring Boot`组件化思想，易于学习理解
- **统一** ：目前已集成了微服务开发常用组件和统一配置
- **扩展** ：组件之间低耦合，高内聚，扩展十分方便

查看[快速开始](quick-start.md)了解详情

## DevOps Boot 能解决什么问题？

- **统一项目配置** ： 免去繁琐的项目配置，gradle插件帮您解决烦恼
- **统一依赖版本管理** ： 多个项目统一jdk和三方依赖版本，避免版本冲突
- **统一微服务治理解决方案**： 解决多个项目技术方案参差不齐，架构不统一问题
- **统一常用工具类** ： 避免代码重复

## 功能特性
- 提供gradle快速开发插件[devops-boot-gradle-plugin](/plugin/devops-boot-gradle-plugin.md)
- 提供gradle快速发布插件[devops-publish-gradle-plugin](/plugin/devops-publish-gradle-plugin.md)
- 提供统一版本依赖管理[devops-boot-dependencies](/dependency/devops-boot-dependencies.md)
- 提供多个开箱即用的starter组件
    - [starter-api](/starter/devops-boot-starter-api.md)
    - [starter-logging](/starter/devops-boot-starter-logging.md)
    - [starter-web](/starter/devops-boot-starter-web.md)
    - [starter-service](/starter/devops-boot-starter-service.md)
    - [starter-plugin](/starter/devops-boot-starter-plugin.md)
    - [starter-schedule](/starter/devops-boot-starter-schedule.md)
    - ...

## 核心依赖

| 依赖          | 版本          |
| ------------ | ------------- |
| JDK          | 1.8+          |
| Kotlin       | 1.6.21        |
| Gradle       | 6.8.3         |
| Spring Boot  | 2.6.13         |
| Spring Cloud | 2021.0.5      |

## 示例

查看[Sample示例](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample)来了解如何优雅集成`devops-boot`框架
