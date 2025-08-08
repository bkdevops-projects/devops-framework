# devops-boot-gradle-plugin

## 功能列表

`devops-boot-gradle-plugin`是一个用于快速构建`Spring Boot`应用程序的gradle插件，引入该插件后，会为开发者自动完成：

- 配置jdk插件及编译选项，默认版本为17
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
    id("com.tencent.devops.boot") version ${version}
}
```

- **build.gradle**

```groovy
plugins {
    id 'com.tencent.devops.boot' version ${version}
}
```

## 配置属性

支持在`gradle.properties`中进行如下配置:

| 属性               | 类型    | 默认值   | 说明               |
| ------------------ | ------- |-------| ------------------ |
| devops.kotlin      | boolean | true  | 是否添加kotlin支持 |
| devops.javaVersion | string  | 17    | jdk版本            |
| devops.copyWithVersion | boolean  | false | 拷贝jar到release目录时是否带版本号           |
| devops.assemblyMode     | string  | null  | 支持consul/k8s/kubernetes,默认使用consul  |

## 功能介绍

### 1. 配置maven仓库列表
从上到下顺序依次为，
1. [Tencent Mirrors](https://mirrors.tencent.com/nexus/repository/maven-public/)
2. mavenCentral
3. jcenter
4. [MavenSnapshotRepo](https://central.sonatype.com/repository/maven-snapshots/)

### 2. 配置依赖管理
- 添加`dependency-management`插件
- 添加`devops-boot-dependencies`bom依赖

### 3. jdk插件及编译配置
- 添加`java`插件
- 配置编译选项
    - sourceCompatibility=<devops.javaVersion>
    - options.encoding=UTF-8

### 4. kotlin插件及编译配置
如果` devops.kotlin=true`，则,
- 添加`kotlin-jvm`插件
- 添加`kotlin-allopen`插件
- 配置编译选项
    - jvmTarget=<devops.javaVersion>
    - freeCompilerArgs=listOf("-Xjsr305=strict", "-java-parameters")
- 添加`kotlin-stdlib-jdk8`、`kotlin-reflect`依赖
- 配置`ktlint`和`ktformat`任务

### 5. JUnit依赖配置
对于非空(包含src目录)模块，
- 添加`org.springframework.boot:spring-boot-starter-test`依赖

### 6. SpringBoot插件配置
对于非空(包含src目录)、且为SpringBoot启动模块(模块命名以boot-开头)，
- 添加`org.springframework.boot`插件
- 配置`copyToRelease`任务
  - 执行`gradle build`后，自动将jar包拷贝到`release`目录下
  - 根据`devops.copyWithVersion`决定拷贝时是否带版本号
- 配置`SpringCloud`支持
  - 根据`devops.assemblyMode`配置，自动添加微服务相关依赖项，目前支持的类型有consul/k8s(不区分大小写)

