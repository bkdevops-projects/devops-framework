# devops-boot-gradle-plugin

## 功能介绍

`devops-boot-gradle-plugin`是一个用于快速构建`Spring Boot`应用程序的gradle插件，引入该插件后，会为我们自动完成：

- 配置jdk插件及编译选项，默认版本为1.8
- 配置maven仓库列表，除中央仓库之外还添加了腾讯源
- 添加`spring boot`相关插件
- 添加依赖管理插件，并引入`devops-boot-dependencies`基础依赖`bom`
- 配置`JUnit`相关支持及依赖
- 支持云原生编译打包
- 支持配置是否引入`kotlin`
- 支持配置`jdk`版本

如果设置了`kotlin`支持，本插件还会进行如下的额外配置：

- 添加`kotlin jvm`插件，并配置`kotlin`相关编译选项
- 添加`spring kotlin`插件，支持`all open`
- 添加`kotlin-stdlib-jdk8`和`kotlin-std-lib-reflect`依赖


## 使用方式

- **build.gradle.kts**

```groovy
plugins {
    id("devops-boot-gradle-plugin") version ${version}
}
```

- **build.gradle**

```groovy
plugins {
    id 'devops-boot-gradle-plugin' version ${version}
}
```

## 配置属性

支持在`gradle.properties`中进行如下配置:

| 属性               | 类型    | 默认值 | 说明               |
| ------------------ | ------- | ------ | ------------------ |
| devops.kotlin      | boolean | true   | 是否添加kotlin支持 |
| devops.javaVersion | string  | 1.8    | jdk版本            |
| devops.copyWithVersion | boolean  | false    | 拷贝jar到release目录时是否带版本号           |
| assembly.mode      | string  | null   | 支持consul/k8s/kubernetes,默认使用consul  |

