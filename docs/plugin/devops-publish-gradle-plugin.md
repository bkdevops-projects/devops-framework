# devops-publish-gradle-plugin

## 功能介绍

`devops-publish-gradle-plugin`是一个用于快速发布jar到maven中央仓库或私有maven仓库的gradle插件。

插件本身基于gradle官方推荐的`maven-publish`插件，我们在不破坏其本身用法的原则上，自动完成以下内容：

- 添加`maven-publish`插件，该插件为gradle官方推荐的jar包发布插件
- 添加`signing`签名插件，该插件为gradle官方推荐的jar包签名插件
- 自动配置签名信息
- 自动配置`javaSource`、`javaDoc`等`publications`
- 自动配置发布仓库信息`repositories`，根据版本号自动选择`repository`
- 自动补全`manifest`信息

`devops-publish-gradle-plugin`插件可以为我们解决以下问题：
1. `SONATYPE`中央仓库对于jar包的规范十分严格，如`manifest`文件、`pom`信息、签名规范等，任何一个环节出错都会导致发布失败
2. 远程仓库信息配置方式不统一
3. GPG签名配置方式不统一

## 使用方式

对于需要发布到中央仓库或私服的`module`:

- **build.gradle.kts**

```groovy
plugins {
    id("devops-publish-gradle-plugin") version ${version}
}
```

- **build.gradle**

```groovy
plugins {
    id 'devops-publish-gradle-plugin' version ${version}
}
```

## 配置属性

### 优先级

为了适配ci流水线、本地开发等各种环境，且避免敏感信息泄露，插件会按照以下顺序查找配置(优先级越高越靠前)：

1. 命令行变量

```shell
gradle -Dkey=value
```

2. 环境变量

```shell
export key=value
```

3. `gradle.properties`

```
key=value
```

### 远程仓库信息

| 属性            | 类型     | 默认值 | 说明        |
| --------------- | ------- | ------ | ---------- |
| releaseRepoUrl  | string  | [https://oss.sonatype.org/service/local/](https://oss.sonatype.org/service/local/) | release仓库地址，默然为SONATYPE中央仓库地址 |
| snapshotRepoUrl | string  | [https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/) | snapshot仓库地址，默然为SONATYPE中央仓库地址            |
| repoUsername    | string  | null | 仓库认证用户名 |
| repoPassword    | string  | null | 仓库认证密码   |


### 签名信息

| 属性            | 类型     | 默认值 | 说明         |
| --------------- | ------- | ------ | ----------- |
| signingKey      | string  | null | gpg签名key     |
| signingKeyId    | string  | null | gpg签名keyId   |
| signingPassword | string  | null | gpg签名password |


## 说明

- 插件会读取`project.version`变量得到版本号，如果版本号以`SNAPSHOT`结尾，则使用`snapshotRepoUrl`进行发布，且不会执行签名，`SONATYPE`只会对release包进行签名校验。

- 获取gpg 信息
    ```shell
    # secret key, signingKeyId取该值
    gpg --armor --export-secret-key username@email --output private.key
    
    # public key
    gpg --armor --export username@email --output public.key
    
    # keyId，取pub最后8位
    gpg --list-keys
    ```

- 自定义pom信息

    ```kotlin
    publishing {
      publications {
          withType<MavenPublication> {
              pom {
                  name.set(project.name)
                  description.set(project.description ?: project.name)
                  url.set("https://github.com/Tencent/bk-ci")
                  licenses {
                      license {
                          name.set("The MIT License (MIT)")
                          url.set("https://opensource.org/licenses/MIT")
                      }
                  }
                  developers {
                      developer {
                          name.set("bk-ci")
                          email.set("devops@tencent.com")
                          url.set("https://bk.tencent.com")
                          roles.set(listOf("Manager"))
                      }
                  }
                  scm {
                      connection.set("scm:git:git://github.com/Tencent/bk-ci.get")
                      developerConnection.set("scm:git:ssh://github.com/Tencent/bk-ci.git")
                      url.set("https://github.com/Tencent/bk-ci")
                  }
              }
          }
      }
    }
    ```


## 参考

- [`maven-publish`插件官方教程](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [`signing`插件官方教程](https://docs.gradle.org/current/userguide/signing_plugin.html)
