package com.tencent.devops.conventions

import com.tencent.devops.utils.findPropertyOrEmpty
import org.gradle.api.Project

/**
 * 根据不同的打包模式配置SpringCloud微服务相关依赖
 */
class SpringCloudConvention {

    fun apply(project: Project) {
        with(project) {
            val assemblyMode = AssemblyMode.ofValueOrDefault(findPropertyOrEmpty(ASSEMBLY_MODE))
            println("Project[$name] assembly mode: $assemblyMode")
            configurations.all { configuration ->
                configuration.resolutionStrategy.eachDependency {
                    if (it.requested.group == "com.tencent.devops" && it.requested.name == "devops-service") {
                        addServiceDependency(this, configuration.name, assemblyMode)
                    }
                }
            }
        }
    }

    /**
     * 添加微服务相关依赖项
     */
    private fun addServiceDependency(project: Project, configuration: String, mode: AssemblyMode) {
        with(project) {
            when (mode) {
                AssemblyMode.CONSUL -> {
                    dependencies.add(configuration, CONSUL_CONFIG)
                    dependencies.add(configuration, CONSUL_DISCOVERY)
                }
                AssemblyMode.K8S, AssemblyMode.KUBERNETES -> {
                    dependencies.add(configuration, K8S_CONFIG)
                    dependencies.add(configuration, K8S_DISCOVERY)
                }
            }
        }
    }

    companion object {
        /**
         * 打包模式
         */
        private const val ASSEMBLY_MODE = "assembly.mode"

        /**
         * 微服务相关依赖
         */
        private const val CONSUL_CONFIG = "org.springframework.cloud:spring-cloud-starter-consul-config"
        private const val CONSUL_DISCOVERY = "org.springframework.cloud:spring-cloud-starter-consul-discovery"
        private const val K8S_CONFIG = "org.springframework.cloud:spring-cloud-starter-kubernetes-client-config"
        private const val K8S_DISCOVERY = "org.springframework.cloud:spring-cloud-starter-kubernetes-client-discovery"
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
