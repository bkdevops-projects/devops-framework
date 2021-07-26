# devops-boot-starter-plugin

`starter-plugin`组件帮助开发者实现插件机制，动态扩展系统功能

## 功能介绍
- 支持动态加载、卸载插件
- 支持通过`actuator`管理插件
- 支持扩展Point，动态添加逻辑
- 支持扩展Controller，动态增加接口


## 模块介绍

- `com.tencent.devops:devops-plugin-api`
  
  插件开发api包，api模块中引入
- `com.tencent.devops:devops-plugin-core`
  
  插件核心逻辑实现，biz模块中引入
- `com.tencent.devops:devops-plugin-processor`
  
  插件开发注解处理逻辑，插件中引入
- `com.tencent.devops:devops-boot-starter-plugin`
  
  插件starter组件，biz模块中引入

## 基本概念
- **插件**

开发者针对系统支持扩展的功能点进行自定义实现，并打包为一个jar包，这个jar包就是插件。
   
- **扩展点**

侠义：系统中预留的支持自由扩展的功能点，在代码中通常定义为一个Interface接口，具体实现交由插件开发者自定义实现，一个插件中可以针对扩展点有N个不同的实现。

广义：包含了预留的扩展点以及下面的扩展接口。

- **扩展接口**

此处的**接口**应理解为对外提供的http api接口，当系统本身的api接口无法满足复杂的变化需求时，可以通过**扩展接口**功能来增加额外的api接口。

## 插件管理

### 1.配置属性

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| plugin.path  | string | ./plugins  | 插件目录，系统将从该目录加载插件 |

### 2.动态加载/卸载

系统第一次启动时，将从`plugin.path`加载插件，在运行时可以通过`actuator`方式管理插件.

- 查看已加载插件
  - GET /actuator/plugin
- 重新加载所有插件
  - POST /actuator/plugin
- 重新加载指定插件
  - POST /actuator/plugin/<plugin-id>
- 卸载指定插件
  - POST /actuator/plugin/<plugin-id>

**注：生产环境请对actuator相关接口设置认证访问**
  
## 插件编写

需求：系统需要对用户输入的内容进行打印，但打印的方式无法确定，如System IO、Logger、Printer等，因此具体的打印逻辑交由插件开发者实现。

参考[sample项目](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample)

### 1. 定义扩展点

扩展点通常定义在项目的`api`模块中，插件开发者只需引入`api`模块，不用关心具体内部逻辑

```kotlin
/**
 * 定义扩展点，必须实现ExtensionPoint接口
 */
interface PrintExtension: ExtensionPoint {

    /**
     * print content
     * 可以通过插件自定义实现打印逻辑，如System IO、Logger、Printer等
     */
    fun print(content: String)
}
```

### 2. 编写插件

插件通常单独在一个项目中编写，与原项目代码解耦。

项目结构参考[plugin-printer](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample/plugin-printer)

扩展点需要使用`@Extension`标注，用于代码打包时自动写入spi注册文件中,

```kotlin
@Extension
class SystemPrintExtension: PrintExtension {
    override fun print(content: String) {
        println(content)
    }
}
```

### 3. 使用扩展点
通过插件管理类`PluginManager`，查找`PrintExtension`类型的扩展点并调用其`print`方法,

```kotlin
class PrintService(private val pluginManager: PluginManager) {
    
    /**
     * print content
     */
    fun print(content: String) {
        // 查找PrintExtension类型的扩展点并应用
        pluginManager.applyExtension<PrintExtension> { 
            print(content) 
        }
    }
}
```

### 4. 插件开发配置文件

插件开发涉及到一些配置属性，如[gradle.propreties](https://github.com/bkdevops-projects/devops-framework/tree/master/devops-boot-sample/plugin-printer/gradle.properties)

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| pluginId  | string | null  | 插件id，要求全局唯一 |
| pluginVersion | string | null  | 插件版本 |
| pluginScope | string | null  | 插件生效范围，*代码应用到所有微服务 |
| pluginAuthor | string | null  | 插件作者 |
| pluginDescription | string | null  | 插件描述 |
