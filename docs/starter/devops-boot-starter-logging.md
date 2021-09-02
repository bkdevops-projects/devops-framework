# devops-boot-starter-logging

`starter-logging`组件帮助开发者完成日志的快速配置，并统一日志格式

## 功能列表
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
devops-logging模块支持springboot本身的logging配置，在此基础上，增添了个性化配置

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| devops.logging.path | string | /logs/${应用名}  | 日志输出位置，默认当前目录 |
| devops.logging.file-app | string | ${应用名}.log | app日志文件名 |
| devops.logging.file-error | string | ${应用名}-error.log | error日志文件名 |
| devops.logging.file-pattern | string | 见如下说明 | 日志文件输出格式 |


默认file-pattern: `%d{yyyy-MM-dd HH:mm:ss.SSS}|%X{ip:--}|%F|%L|%level|%X{err_code:-0}|||||[%t] %m%ex%n`
## 日志profile说明

logging组件会自动根据不同的`profile`应用不同的appender配置

| profile                   | 初始日志级别 | appender           |
| ------------------------- | ----------- | ----------------- |
| `default`、`local`、`dev`  | INFO        | `console`、`file` |
| `test`、`prod`             | INFO        | `file`            |
