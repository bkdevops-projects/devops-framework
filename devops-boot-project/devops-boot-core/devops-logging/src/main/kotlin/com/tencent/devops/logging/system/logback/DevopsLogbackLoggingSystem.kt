package com.tencent.devops.logging.system.logback

import org.springframework.boot.logging.LoggingSystemProperties
import org.springframework.boot.logging.logback.LogbackLoggingSystem
import org.springframework.core.env.ConfigurableEnvironment

/**
 * devops-framework在logback框架下的log system
 */
class DevopsLogbackLoggingSystem(
    classLoader: ClassLoader
) : LogbackLoggingSystem(classLoader) {

    override fun getSystemProperties(environment: ConfigurableEnvironment): LoggingSystemProperties {
        return DevopsLogbackSystemProperties(environment)
    }
}
