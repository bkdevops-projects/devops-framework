package com.tencent.devops.service

import org.springframework.boot.SpringApplication
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import java.util.*

/**
 * 用于向ConfigDataEnvironment注入公共配置，用于引导springboot统一加载外部配置
 *
 * `spring.config.import`配置项用于引入外部配置，由`ConfigDataEnvironmentPostProcessor`处理，不能通过@PropertySource配置，
 * 通过@PropertySource引入的配置，在spring context初始化完毕之前不会被添加到Environment中，
 * 而`spring.config.import`在`ConfigDataEnvironmentPostProcessor`中通过`ConfigDataEnvironment`解析，
 * 所以在`ConfigDataEnvironmentPostProcessor`之前配置写入到ConfigDataEnvironment
 */
class ConfigImportEnvironmentPostProcessor : EnvironmentPostProcessor, Ordered {

    override fun getOrder(): Int {
        return ConfigDataEnvironmentPostProcessor.ORDER - 1
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        environment.propertySources.addLast(createPropertySource())
    }

    private fun createPropertySource(): PropertiesPropertySource {
        val properties = Properties().apply {
            setProperty("spring.config.import", OPTIONAL_CONSUL)
            setProperty("spring.application.name", APPLICATION_NAME_PATTERN)
        }
        return PropertiesPropertySource(SOURCE_NAME, properties)
    }

    companion object {
        private const val SOURCE_NAME = "devopsProperties"
        private const val OPTIONAL_CONSUL = "optional:consul:"
        private const val APPLICATION_NAME_PATTERN = "\${service.prefix:}\${service.name}\${service.suffix:}"
    }
}
