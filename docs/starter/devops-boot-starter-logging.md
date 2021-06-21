# devops-boot-starter-logging

`starter-logging`组件帮助开发者完成日志的快速配置，并统一日志格式

## 功能介绍
- 基于`slf4j`+`logback`
- 提供`logback`基础配置文件，支持`include`方式引入
    - `base.xml` 基础属性配置
    - `appender.xml` `appender`配置
- 配置统一的日志打印格式
- 配置统一的日志输出位置
- 配置统一的日志切割策略
- 配置统一的异步日志输出
- 约定统一的日志分类方式
    - 访问日志: {application}-access.log
    - 应用日志: {application}-app.log
    - 错误日志: {application}-error.log

## 使用方式
- **build.gradle.kts**

```kotlin
implementation("com.tencent.devops:devops-boot-starter-logging")
```

- **build.gradle**

```groovy
implementation 'com.tencent.devops:devops-boot-starter-logging'
```

## 配置属性

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| logging.file.path  | string | ./  | 日志输出位置，默认当前目录 |
| logging.level.\<loggerName\> | string | INFO | 调整`loggerName`日志级别, `root`代表所有logger |

## 说明

logging组件会自动根据不同的`profile`应用不同的appender配置

| profile                   | 初始日志级别 | appender           |
| ------------------------- | ----------- | ----------------- |
| `default`、`local`、`dev`  | INFO        | `console`、`file` |
| `test`、`prod`             | INFO        | `file`            |
