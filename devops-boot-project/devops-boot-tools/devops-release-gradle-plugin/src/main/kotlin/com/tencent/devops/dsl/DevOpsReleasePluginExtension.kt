package com.tencent.devops.dsl

import org.gradle.api.provider.Property

interface DevOpsReleasePluginExtension {
    /**
     * scm url
     * */
    val scmUrl: Property<String>

    /**
     * 开发版本的快照后缀
     * */
    val snapshotSuffix: Property<String>

    /**
     * SemVer的增长策略
     * 可选值MAJOR, MINOR, PATCH
     * */
    val incrementPolicy: Property<String>
}
