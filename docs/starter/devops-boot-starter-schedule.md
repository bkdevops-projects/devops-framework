# devops-boot-starter-schedule

`starter-schedule`组件提供分布式调度功能，帮助开发者快速接入分布式调度

## 设计思路

借鉴`xxl-job`的思想，将调度中心和执行器完全解耦，保持`xxl-job`的轻量化特性的同时，还具有
- 可插拔设计
- 与Spring Cloud无缝集成
- 可扩展设计
- 支持多种数据源，JDBC、MongoDB...

## 基本概念
- **server**

  调度中心，负责任务任务的调度。已经开发好，直接启动即可。
- **worker**

  任务执行者，由开发者自定义开发任务逻辑，和server解耦。
- **worker group**

  工作组，代表一组worker。一个任务绑定到一个任务工作组，由调度中心根据不同策略调度到工作组中的worker执行。
- **job**

  任务信息
- **job log**

  任务调度（执行）记录。一个任务可能被调度多次（cron表达式执行、定时执行），每次执行将产生一次执行记录。
- **worker注册模式**
  - AUTO worker启动后自动注册信息到server
  - DISCOVERY 通过服务发现获取worker信息，无需注册，在`Spring Cloud`环境下使用
  - MANUAL 在调度中心手动注册worker信息

## 模块介绍

- `com.tencent.devops:devops-schedule-common`

  worker和server公共模块
- `com.tencent.devops:devops-schedule-model`

  server数据模型抽象模块
- `com.tencent.devops:devops-schedule-model-mongodb`

  基于MongoDB实现的server数据模型模块
- `com.tencent.devops:devops-schedule-server`

  调度中心server模块，包含了调度核心逻辑和前端ui。ui基于`avue`开发。
- `com.tencent.devops:devops-schedule-worker`

  任务执行器worker模块
- `com.tencent.devops:devops-schedule-server-starter`

  调度中心server starter组件
- `com.tencent.devops:devops-schedule-worker-starter`

  任务执行器worker starter组件

## 使用方式

参考[Server Sample项目](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample/boot-schedule-server-sample)
参考[Worker Cloud Sample项目](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample/boot-schedule-worker-cloud-sample)

1. 创建调度中心项目(资源有限情况下也可以在已有项目中引入)
- **build.gradle.kts**

```kotlin
// 引入server starter包，默认使用model-mongodb实现
implementation("com.tencent.devops:devops-boot-starter-schedule-server-starter")
```

2. 创建任务执行者项目
- **build.gradle**

```kotlin
// 引入worker starter包，
implementation("com.tencent.devops:devops-boot-starter-schedule-worker-starter")
```

3. 编写任务逻辑
- **创建JobHandler Bean**

```kotlin
@Component("SampleJobHandler")
class SampleJobHandler: JobHandler {

    override fun execute(context: JobContext): JobExecutionResult {
        // 通过jobParamMap获取任务参数
        val name = context.jobParamMap["name"].toString()
        println("Hello, $name!")
        // 返回执行结果
        return JobExecutionResult.success()
    }
}
```

4. 在调度中心创建工作组、任务信息，调度中心将根据调度配置自动触发`SampleJobHandler.execute()`方法


## 配置属性

- 调度中心server配置

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| devops.schedule.server.enabled  | boolean | true  | 是否开启调度中心 |
| devops.schedule.server.context-path  | string | ""  | 调度中心访问地址context path |
| devops.schedule.server.maxTriggerPoolSize  | int | 100  | 任务触发线程池大小 |
| devops.schedule.server.ui.enabled  | boolean | true  | 是否开启调度中心ui界面 |
| devops.schedule.server.auth.access-token  | string | secret  | 调度中心和执行器之间的accessToken |
| devops.schedule.server.auth.username  | string | admin  | 前端ui访问用户名 |
| devops.schedule.server.auth.password  | string | password  | 前端ui访问密码 |
| devops.schedule.server.auth.secret-key  | string | secret  | 前端jwt加密密钥 |
| devops.schedule.server.auth.expiration  | duration | 0  | 前端jwt加密过期时间，默认永不过期 |

- 执行器worker配置

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| devops.schedule.worker.mode  | string | AUTO  | 注册模式 |
| devops.schedule.worker.group  | string | ""  | 工作组名称，为空则使用自身的应用名称。仅当mode=AUTO时有效 |
| devops.schedule.worker.address  | string | ""  | 自身地址，为空则自动获取。仅当mode=AUTO时有效 |
| devops.schedule.worker.executor.maximumPoolSize  | int | 100  | 执行器最大线程数 |
| devops.schedule.worker.executor.corePoolSize  | int | 1  | 执行器核心线程数 |
| devops.schedule.worker.executor.keepAliveTime  | int | 60 | 执行器线程存活时间，单位秒 |
| devops.schedule.worker.server.access-token  | string | secret | 调度中心和执行器之间的accessToken |
| devops.schedule.worker.server.address  | string | http://localhost:8080 | 调度中心地址，DISCOVERY模式下可填写服务名称 |

## 参考

- `xxl-job`[xxl-job](http://springfox.github.io/springfox/docs/current/)
- `avue`[avue](https://avuejs.com/docs/home.html)
