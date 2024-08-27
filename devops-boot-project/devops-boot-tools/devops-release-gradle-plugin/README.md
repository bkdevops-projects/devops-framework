# devops-release-gradle-plugin

## 功能介绍
`devops-release-gradle-plugin`是一个用于自动化发布的一个插件，帮助我们管理项目版本,简化发布流程，自动完成以下内容：

- 检查是否有未提交的内容
- 去掉-SNAPSHOT,生成发布版本号
- 根据发布版本号，自动打tag
- 更新版本号到下一个开发版本
- 对版本的变化进行版本控制

`devops-release-gradle-plugin`插件可以为我们解决以下问题：

1. 繁琐的修改版本号工作
2. 项目版本变化缺乏版本控制
3. 发布失败时自动回滚

## 使用方式

1. 创建version.txt，并写入版本信息（如1.0.0-SNAPSHOT）
2. 添加插件并使用version.txt作为项目版本

**Kotlin DSL Example**

```groovy
plugins {
    id("com.tencent.devops.release") version ${version}
}

version = file("version.txt").readText().trim()
```

运行插件

```shell
gradle release
```

## 配置插件

默认是根据version.txt文件，自动生成发布版本，并根据SemVer，增加MINOR，生成下一个开发版本，标签则是在发布版本前增加v。

可以使用gradle generateReleaseProperties，预览发布插件自动生成的信息。

所以我们只需添加如下配置：

```groovy
release {
    scmUrl.set("scm:git:xxx.git")
}
```

目前版本库只支持git，有其他需要可以再增加。

完整配置

```groovy
release {
    scmUrl.set("scm:git:xxx.git") // 设置scm url
    snapshotSuffix.set("-SNAPSHOT") // 设置快照后缀，默认是'-SNAPSHOT'
    incrementPolicy.set("MINOR") // 设置语义化版本增长策略（MAJOR, MINOR, PATCH），默认是MINOR
}
```

当然我们也可以指定发布参数，例如：

```shell
gradle release -Prelease.releaseVersion=1.1.0 -Prelease.developmentVersion=1.2.0-SNAPSHOT -Prelease.tagName=v1.1.0
```
## 多项目构建

`devops-release-gradle-plugin`约定和希望根项目与子项目是相同的版本，所以在多项目的情况下可以如下配置：

```groovy
val singleVersion = file("version.txt").readText().trim()
allprojects {
    group = "org.example"
    version = singleVersion
}
```