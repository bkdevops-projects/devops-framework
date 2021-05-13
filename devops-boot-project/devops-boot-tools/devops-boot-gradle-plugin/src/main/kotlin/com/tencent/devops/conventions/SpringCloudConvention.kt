package com.tencent.devops.conventions

import com.tencent.devops.utils.findPropertyOrEmpty
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.IMPLEMENTATION

/**
 * 根据不同的打包模式配置SpringCloud微服务相关依赖
 */
class SpringCloudConvention {

    fun apply(project: Project) {
        with(project) {
            val assemblyMode = resolveAssemblyMode(this)
            addServiceDependency(this, assemblyMode)
            // 下面的方式能够实现效果，但在idea中无法看到依赖
//            configurations.all { configuration ->
//                configuration.resolutionStrategy.eachDependency {
//                    if (it.requested.group == "com.tencent.devops" && it.requested.name == "devops-service") {
//                        addServiceDependency(this, configuration.name, assemblyMode)
//                    }
//                }
//            }
        }
    }

    /**
     * 解析打包模式
     */
    private fun resolveAssemblyMode(project: Project): AssemblyMode {
        val property = project.findPropertyOrEmpty(ASSEMBLY_MODE)
        return AssemblyMode.ofValueOrDefault(property.trim()).apply {
            println("Project[${project.name}] assembly mode: $this")
        }
    }

    /**
     * 添加微服务相关依赖项
     */
    private fun addServiceDependency(project: Project, mode: AssemblyMode) {
        when (mode) {
            AssemblyMode.CONSUL -> {
                project.dependencies.add(IMPLEMENTATION, CONSUL_CONFIG)
                project.dependencies.add(IMPLEMENTATION, CONSUL_DISCOVERY)
            }
            AssemblyMode.K8S, AssemblyMode.KUBERNETES -> {
                project.dependencies.add(IMPLEMENTATION, K8S_CONFIG)
                project.dependencies.add(IMPLEMENTATION, K8S_DISCOVERY)
            }
        }
    }

    companion object {
        /**
         * 打包模式
         */
        private const val ASSEMBLY_MODE = "assembly.mode"

        /**
         * consul依赖
         */
        private const val CONSUL_CONFIG = "org.springframework.cloud:spring-cloud-starter-consul-config"
        private const val CONSUL_DISCOVERY = "org.springframework.cloud:spring-cloud-starter-consul-discovery"

        /**
         * kubernetes依赖
         */
        private const val K8S_CONFIG = "org.springframework.cloud:spring-cloud-starter-kubernetes-client"
        private const val K8S_DISCOVERY = "org.springframework.cloud:spring-cloud-starter-kubernetes-client-config"
    }
}

enum class AssemblyMode {
    CONSUL,
    K8S,
    KUBERNETES;

    companion object {
        fun ofValueOrDefault(value: String): AssemblyMode {
            val upperCase = value.toUpperCase()
            return values().find { it.name == upperCase } ?: CONSUL
        }
    }
}
