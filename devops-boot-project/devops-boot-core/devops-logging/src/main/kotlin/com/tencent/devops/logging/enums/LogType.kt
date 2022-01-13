package com.tencent.devops.logging.enums

import com.tencent.devops.logging.system.java.DevopsJavaLoggingSystem
import com.tencent.devops.logging.system.log4j.DevopsLog4J2LoggingSystem
import com.tencent.devops.logging.system.logback.DevopsLogbackLoggingSystem

enum class LogType(val className: String) {
    LOGBACK(DevopsLogbackLoggingSystem::class.java.name),
    LOG4J2(DevopsLog4J2LoggingSystem::class.java.name),
    JAVA(DevopsJavaLoggingSystem::class.java.name)
}
