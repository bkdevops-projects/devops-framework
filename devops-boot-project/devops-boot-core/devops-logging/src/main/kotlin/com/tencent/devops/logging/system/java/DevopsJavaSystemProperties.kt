package com.tencent.devops.logging.system.java

import com.tencent.devops.logging.system.common.ICommonLoggingProperties
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.LoggingSystemProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertyResolver

/**
 * devops-framework的java系统变量
 */
class DevopsJavaSystemProperties(environment: Environment) :
    LoggingSystemProperties(environment), ICommonLoggingProperties {

    override fun apply(logFile: LogFile?, resolver: PropertyResolver) {
        super.apply(logFile, resolver)
        applyDevopsProperties(resolver) { name, value -> setSystemProperty(name, value) }
    }
}
