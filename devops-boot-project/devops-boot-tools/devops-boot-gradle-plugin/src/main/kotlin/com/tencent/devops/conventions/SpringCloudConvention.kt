package com.tencent.devops.conventions

import com.tencent.devops.enums.AssemblyMode
import com.tencent.devops.utils.resolveAssemblyMode
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

            AssemblyMode.NONE -> {
                // 独立部署，不依赖任何微服务环境
            }
        }
    }

    companion object {
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
