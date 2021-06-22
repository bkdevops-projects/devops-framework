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

- `SONATYPE_USERNAME`  sonatype用户名
- `SONATYPE_PASSWORD`  sonatype密码
- `ORG_GRADLE_PROJECT_signingKey`  gpg签名key
- `ORG_GRADLE_PROJECT_signingKeyId`  gpg签名key id
- `ORG_GRADLE_PROJECT_signingPassword`  gpg签名密码

配合github流水线自动发布时, 项目中编写的github流水线[publish.yml](../.github/workflows/publish.yml)
会自动读取以下`github secrets`并设置为对应的环境变量：

- `secrets.SONATYPE_USERNAME`
- `secrets.SONATYPE_PASSWORD`
- `secrets.SIGNING_KEY`
- `secrets.SIGNING_KEY_ID`
- `secrets.SIGNING_PASSWORD`
