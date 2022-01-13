package com.tencent.devops.logging.system.common

import com.tencent.devops.logging.enums.LogAppenderType
import org.springframework.core.env.PropertyResolver
import java.io.File

interface ICommonLoggingProperties {

    companion object {
        const val LOGGING_PREFIX = "devops.logging."
    }

    fun applyDevopsProperties(propertyResolver: PropertyResolver, setter: (String, String) -> Unit) {
        val applicationName = propertyResolver.getProperty("spring.application.name", "application")
        var loggingPath = propertyResolver.getProperty("${LOGGING_PREFIX}path", "logs/$applicationName").trim()
        val pathDir = File(loggingPath)
        if (pathDir.exists() && !pathDir.isDirectory) {
            loggingPath = "logs/$applicationName"
        }
        if (!loggingPath.endsWith("/") && !loggingPath.endsWith("\\")) {
            loggingPath = loggingPath.plus("/")
        }
        LogAppenderType.values().forEach {
            setFileProperty(
                resolver = propertyResolver,
                applicationName = applicationName,
                loggingPath = loggingPath,
                logType = it,
                setter = setter
            )
        }
    }

    fun setFileProperty(resolver: PropertyResolver, loggingPath: String, applicationName: String, logType: LogAppenderType, setter: (String, String) -> Unit) {
        if (logType.fileName != null) {
            val logSuffix = if (logType.fileName.suffix) "-${logType.fileName.aliasName}" else ""
            val loggingFile = resolver.getProperty("$LOGGING_PREFIX${logType.fileName.aliasName}-file", "$applicationName$logSuffix.log")
            setter.invoke(logType.fileName.fileNameValue, "$loggingPath$loggingFile")
        }
        if (logType.filePattern != null) {
            val filePattern = resolver.getProperty("${LOGGING_PREFIX}file-pattern", logType.filePattern.defaultValue)
            setter.invoke(logType.filePattern.filePatternValue, filePattern)
        }
    }
}
