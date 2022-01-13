package com.tencent.devops.logging.system.java

import org.springframework.boot.logging.LoggingSystemProperties
import org.springframework.boot.logging.java.JavaLoggingSystem
import org.springframework.core.env.ConfigurableEnvironment

/**
 * devops-framework在java框架下的log system
 */
class DevopsJavaLoggingSystem(
    classLoader: ClassLoader
) : JavaLoggingSystem(classLoader) {

    override fun getSystemProperties(environment: ConfigurableEnvironment): LoggingSystemProperties {
        return DevopsJavaSystemProperties(environment)
    }
}
