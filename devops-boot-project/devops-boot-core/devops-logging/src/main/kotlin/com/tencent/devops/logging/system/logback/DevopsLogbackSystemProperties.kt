package com.tencent.devops.logging.system.logback

import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertyResolver
import java.io.File

/**
 * devops-framework的logback系统变量
 */
class DevopsLogbackSystemProperties(environment: Environment) :
    LogbackLoggingSystemProperties(environment) {

    companion object {
        private const val LOGGING_PREFIX = "devops.logging."
    }

    override fun apply(logFile: LogFile?, resolver: PropertyResolver) {
        super.apply(logFile, resolver)
        applyDevopsProperties(resolver)
    }

    private fun applyDevopsProperties(propertyResolver: PropertyResolver) {
        val applicationName = propertyResolver.getProperty("spring.application.name", "application")
        var loggingPath = propertyResolver.getProperty("${LOGGING_PREFIX}path", "logs/$applicationName").trim()
        val pathDir = File(loggingPath)
        if (pathDir.exists() && !pathDir.isDirectory) {
            loggingPath = "logs/$applicationName"
        }
        if (!loggingPath.endsWith("/") && !loggingPath.endsWith("\\")) {
            loggingPath = loggingPath.plus("/")
        }
        LogType.values().forEach {
            setFileProperty(
                resolver = propertyResolver,
                applicationName = applicationName,
                loggingPath = loggingPath,
                logType = it
            )
        }
    }

    private fun setFileProperty(resolver: PropertyResolver, loggingPath: String, applicationName: String, logType: LogType) {
        if (logType.fileName != null) {
            val logSuffix = if (logType.fileName.suffix) "-${logType.fileName.aliasName}" else ""
            val loggingFile = resolver.getProperty("${LOGGING_PREFIX}${logType.fileName.aliasName}-file", "$applicationName$logSuffix.log")
            setSystemProperty(logType.fileName.fileNameValue, "$loggingPath$loggingFile")
        }
        if (logType.filePattern != null) {
            val filePattern = resolver.getProperty("${LOGGING_PREFIX}file-pattern", logType.filePattern.defaultValue)
            setSystemProperty(logType.filePattern.filePatternValue, filePattern)
        }
    }
}

/**
 * 定义不同类型的枚举
 */
enum class LogType(
    val fileName: FileName?,
    val filePattern: FilePattern?
) {
    CONSOLELOG(
        null,
        FilePattern(
            filePatternValue = "logging.console.pattern",
            defaultValue = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint}|%X{tid:--}|%clr(%-35.35logger{34}){cyan}|%clr(%-3L){magenta}|%clr(%5level)|%X{ip:--}|%X{uid:--}|-|-|%clr(%-14.14t){faint}| %m%n%ex"
        )
    ),
    APPLOG(
        FileName(
            aliasName = "app",
            suffix = false,
            fileNameValue = "logging.app.file"
        ),
        FilePattern(
            filePatternValue = "logging.app.file.pattern",
            defaultValue = "%d{yyyy-MM-dd HH:mm:ss.SSS}|%X{tid:--}|%-35.35logger{34}|-|%5level|%X{ip:--}|%X{uid:--}|-|-|%-14.14t| %m%n%ex"
        )
    ),
    ERRORLOG(
        FileName(
            aliasName = "error",
            suffix = true,
            fileNameValue = "logging.error.file"
        ),
        FilePattern(
            filePatternValue = "logging.error.file.pattern",
            defaultValue = "%d{yyyy-MM-dd HH:mm:ss.SSS}|%X{tid:--}|%-35.35logger{34}|%-3L|%5level|%X{ip:--}|%X{uid:--}|-|-|%-14.14t| %m%n%ex"
        )
    ),
    ACCESSLOG(
        FileName(
            aliasName = "access",
            suffix = true,
            fileNameValue = "logging.access.file"
        ),
        null
    )
}

/**
 * 文件名属性
 */
data class FileName(
    val aliasName: String,
    val suffix: Boolean,
    val fileNameValue: String
)

/**
 * 日志格式属性
 */
data class FilePattern(
    val filePatternValue: String,
    val defaultValue: String
)
