package com.tencent.devops.service

import org.springframework.boot.SpringApplication
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.util.ClassUtils
import java.util.Properties

/**
 * 向ConfigDataEnvironment注入公共配置，用于引导springboot统一加载外部配置
 *
 * TODO: SpringCloud 2020.0+ 默认关闭了bootstrap context, 但SpringCloud Kubernetes还是依赖了bootstrap.yaml,
 * 等SpringCloud Kubernetes不依赖bootstrap context时，可以改造移除项目中的bootstrap.yaml
 */
class ServiceBootstrapEnvironmentPostProcessor: EnvironmentPostProcessor, Ordered {

    // Before ConfigFileApplicationListener
    override fun getOrder(): Int {
        return  ConfigDataEnvironmentPostProcessor.ORDER - 1
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        if (environment.propertySources.contains(BOOTSTRAP_SOURCE_NAME)) {
            environment.propertySources.addLast(createPropertySource())
        }
    }

    private fun createPropertySource(): PropertiesPropertySource {
        with(Properties()) {
            if (isConsulPresent()) {
                setProperty("spring.cloud.consul.config.name", SERVICE_NAME)
                setProperty("spring.cloud.consul.config.prefix", CONSUL_CONFIG_PREFIX)
                setProperty("spring.cloud.consul.config.format", CONSUL_CONFIG_FORMAT)
                setProperty("spring.cloud.consul.config.profile-separator", CONSUL_CONFIG_SEPARATOR)
                setProperty("spring.cloud.consul.discovery.service-name", SERVICE_NAME)
                setProperty("spring.cloud.consul.discovery.instance-id", SERVICE_INSTANCE_ID)
                setProperty("spring.cloud.consul.discovery.query-passing", "true")
                setProperty("spring.cloud.consul.discovery.prefer-ip-address", "true")
                setProperty("spring.cloud.consul.discovery.heath-check-path", "/actuator/health")
                setProperty("spring.cloud.consul.discovery.heath-check-interval", "10s")
                setProperty("spring.cloud.consul.discovery.heath-check-timeout", "5s")
            }
            if (isK8sPresent()) {
                setProperty("spring.cloud.kubernetes.config.sources[0].name", K8S_COMMON_CONFIG)
                setProperty("spring.cloud.kubernetes.config.sources[1].name", SERVICE_NAME)
            }
            return PropertiesPropertySource(DEVOPS_SOURCE_NAME, this)
        }
    }

    /**
     * 判断consul依赖是否存在
     */
    private fun isConsulPresent(): Boolean {
        return isPresent(CONSUL_CLASS_NAME)
    }

    /**
     * 判断k8s依赖是否存在
     */
    private fun isK8sPresent(): Boolean {
        return isPresent(K8S_CLASS_NAME)
    }

    /**
     * 判断类名为[className]的类是否存在
     */
    private fun isPresent(className: String): Boolean {
        return try {
            forName(className, ClassUtils.getDefaultClassLoader())
            true
        } catch (ex: Throwable) {
            false
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun forName(className: String, classLoader: ClassLoader?): Class<*>? {
        return classLoader?.loadClass(className) ?: Class.forName(className)
    }

    companion object {
        private const val BOOTSTRAP_SOURCE_NAME = "bootstrap"
        private const val DEVOPS_SOURCE_NAME = "devopsProperties"
        private const val CONSUL_CONFIG_FORMAT = "YAML"
        private const val CONSUL_CONFIG_SEPARATOR = "::"
        private const val SERVICE_NAME = "\${service.prefix:}\${spring.application.name}\${service.suffix:}"
        private const val SERVICE_INSTANCE_ID = "$SERVICE_NAME-\${server.port}-\${spring.cloud.client.ip-address}"
        private const val CONSUL_CONFIG_PREFIX = "\${service.prefix:}config\${service.suffix:}"
        private const val K8S_COMMON_CONFIG = "\${service.prefix:}common\${service.suffix:}"
        private const val CONSUL_CLASS_NAME = "org.springframework.cloud.consul.config.ConsulConfigAutoConfiguration"
        private const val K8S_CLASS_NAME = "org.springframework.cloud.kubernetes.commons." +
            "KubernetesCommonsAutoConfiguration"
    }
}
