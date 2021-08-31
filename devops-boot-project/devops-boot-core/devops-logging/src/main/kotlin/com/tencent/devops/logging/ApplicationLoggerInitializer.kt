package com.tencent.devops.logging

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class ApplicationLoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val environment = applicationContext.environment
        val applicationName = environment.getProperty("spring.application.name", "application")
        val loggingFilePath = environment.getProperty("logging.file.path", "logs")
        val loggingFileName = "%s/%s/%s.log".format(loggingFilePath, applicationName, applicationName)
        // 为spring boot admin 提供日志路径用于展示
        System.setProperty(LOGGING_FILE_NAME, loggingFileName)
    }

    companion object {
        private const val LOGGING_FILE_NAME = "logging.file.name"
    }
}
