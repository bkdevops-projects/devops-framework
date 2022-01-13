package com.tencent.devops.logging.system.log4j

import org.springframework.boot.logging.LoggingSystemProperties
import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem
import org.springframework.core.env.ConfigurableEnvironment

/**
 * devops-framework在log4j2框架下的log system
 */
class DevopsLog4J2LoggingSystem(
    classLoader: ClassLoader
) : Log4J2LoggingSystem(classLoader) {

    override fun getSystemProperties(environment: ConfigurableEnvironment): LoggingSystemProperties {
        return DevopsLog4J2SystemProperties(environment)
    }
}
