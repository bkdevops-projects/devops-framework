package com.tencent.devops.conventions

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * JUnit 相关配置
 */
class JUnitConvention {

    /**
     * 配置test任务选项
     */
    fun apply(project: Project) {
        with(project) {
            tasks.withType(Test::class.java) { it.useJUnitPlatform() }
            dependencies.add(TEST_IMPLEMENTATION, TEST_DEPENDENCY)
        }
    }

    companion object {
        private const val TEST_IMPLEMENTATION = "testImplementation"
        private const val TEST_DEPENDENCY = "org.springframework.boot:spring-boot-starter-test"
    }
}
