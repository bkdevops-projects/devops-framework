package com.tencent.devops.logging.system.logback

import com.tencent.devops.logging.system.common.ICommonLoggingProperties
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertyResolver

/**
 * devops-framework的logback系统变量
 */
class DevopsLogbackSystemProperties(environment: Environment) :
    LogbackLoggingSystemProperties(environment), ICommonLoggingProperties {

    override fun apply(logFile: LogFile?, resolver: PropertyResolver) {
        super.apply(logFile, resolver)
        applyDevopsProperties(resolver) { name, value -> setSystemProperty(name, value) }
    }
}
