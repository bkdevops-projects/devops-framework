# 参与开发

## 工程结构
?> 待完善

```shell script
devops-framework/
├── buildSrc                      # gradle项目构建目录
├── devops-boot-project           # devops-boot源码目录
│   ├── devops-boot-core          # 核心实现模块
│   ├── devops-boot-dependencies  # maven bom模块
│   ├── devops-boot-starters      # starter组件目录
│   └── devops-boot-tools         # gradle脚本等工具目录
├── devops-boot-sample            # sample项目
└── docs                          # 项目文档
```

### 分支管理
项目采用主干开发模式，主要包含以下几种分支：

* `master` 主干开发分支
* `release-*` 已发布的版本分支
* `...` 其他特性分支，如jdk17

## How to publish to maven repository?

### 发布命令
- 发布jar包到本地仓库，同时会发布插件jar包
```shell script
./gradlew publishToMavenLocal
```

- 发布jar包到sonatype中央仓库，同时会发布插件jar包
```shell script
./gradlew publish
```

- 发布gradle格式插件包到gradle portal
```shell script
./gradlew publishPlugins
```

### 环境变量准备
当需要发布到中央仓库时，会读取以下环境变量：

- `ORG_GRADLE_PROJECT_repoUsername`  sonatype用户名
- `ORG_GRADLE_PROJECT_repoPassword`  sonatype密码
- `ORG_GRADLE_PROJECT_signingKey`  gpg签名key
- `ORG_GRADLE_PROJECT_signingKeyId`  gpg签名key id
- `ORG_GRADLE_PROJECT_signingPassword`  gpg签名密码

配合github流水线自动发布时, 项目中编写的github流水线[release.yml](../.github/workflows/release.yml)
会自动读取以下`github secrets`并设置为对应的环境变量：

- `secrets.SONATYPE_USERNAME`
- `secrets.SONATYPE_PASSWORD`
- `secrets.SIGNING_KEY`
- `secrets.SIGNING_KEY_ID`
- `secrets.SIGNING_PASSWORD`

### 项目发布管理
项目采用语义化版本管理，同时通过github流水线进行自动化发布。自动化发布过程包括版本升级、创建发布分支、
创建tag、创建github release、部署jar包等等

发布需要人工触发，分为以下两个步骤：
1. 创建发布分支
   
   运行`Create release branch` Action。创建发布分支只允许从master或者hotfix-*创建。
2. 发布Jar包
   
   运行`Release` Action，选择要发布的发布分支release-*。发布只允许从release-*发布。
