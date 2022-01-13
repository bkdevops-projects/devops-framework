# 更新日志
## [0.0.6] (待发布)

## [0.0.5](https://github.com/bkdevops-projects/devops-framework/releases/tag/0.0.4) (2022-01-13)

### Features
- 日志路径支持自定义 #96
- 开发分布式调度中心组件schedule-starter #103

### Refactor
- dependencies自定义的版本没有生效 #92
- boot项目没有应用到devops-boot-dependencies #119
- 整理仓库配置 #110
- 仓库列表优化 #95

### Dependencies
- 升级log4j到2.15.0 #111
- 升级logback版本 #114
- feign 10.10.1自动在header的value的逗号后面加空格, 导致获取到的值不符合预期 #89

### Documentation
- documentation: 完善文档 #65

## [0.0.4](https://github.com/bkdevops-projects/devops-framework/releases/tag/0.0.4) (2021-07-29)

### Features
- 支持k8s云原生编译打包方式
- 开发插件化组件plugin-starter
- 支持服务名称前缀后缀
-【服务治理】客户端负载均衡&服务间灰度调用能力
-【服务治理】客户端断路器&限流组件

### Refactor
- copyToRelease支持不带版本号
- gradle插件代码拆分
- 解决jooq项目model模块没有应用java插件问题
- 服务注册instance-id使用hostname
- repository移除mavenLocal
- kotlin编译增加-java-parameters参数

### Dependencies
- kotlin升级1.4.32
- ktlint升级0.41.0
- SpringBoot升级2.4.5
- SpringCloud升级2020.0.2

### Documentation
- 发布文档页面

## [0.0.3](https://github.com/bkdevops-projects/devops-framework/releases/tag/0.0.3) (2021-01-05)

### Features
- 开发api公共组件
- 开发web公共组件
- 开发service公共组件
- 提供HTTP常用枚举和常量

### Docs
- 补全文档

## [0.0.2](https://github.com/bkdevops-projects/devops-framework/releases/tag/0.0.2) (2020-12-22)

### Features
- devops-boot-gradle-plugin仓库列表添加mavenLocal
- 开发devops-publish-gradle-plugin插件

### Chore
- 合并pr后才触发publish snapshot

### Refactor
- gradle插件自动过滤空模块
- 优化devops-boot-gradle-plugin

### Dependencies
- 添加公共依赖版本管理
- 添加公共依赖版本管理

## [0.0.1](https://github.com/bkdevops-projects/devops-framework/releases/tag/0.0.1) (2020-10-09)

### Features:
- 开发devops boot gradle插件
- 开发devops dependencies 依赖管理bom
- 开发devops-logging-starter 日志组件
