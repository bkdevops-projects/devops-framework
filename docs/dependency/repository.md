# Maven仓库配置

## 优先级
```
自定义库
release库
snapshot库
mavenLocal()
```

#### 一. 自定义库
可选值 , 使用 `-DmavenRepoUrl="xxx"` 引入

#### 二. release库
- 当编译环境为Github Action时 , 使用
```
mavenCentral()
gradlePluginPortal()
```
- 其他情况 , 使用
```
https://mirrors.tencent.com/nexus/repository/maven-public/
https://mirrors.tencent.com/nexus/repository/gradle-plugins/
```
#### 三. snapshot库
```
https://central.sonatype.com/content/repositories/snapshots/
```
