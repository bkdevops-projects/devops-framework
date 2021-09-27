package com.tencent.devops.logging.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.LifeCycle
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertyResolver
import org.springframework.core.env.PropertySourcesPropertyResolver
import java.io.File

class CustomLogContextListener : ContextAwareBase(), EnvironmentPostProcessor, LoggerContextListener, LifeCycle, Ordered {

    companion object {
        private lateinit var environment: ConfigurableEnvironment
        const val LOGGING_PREFIX = "devops.logging."
    }

    override fun isResetResistant(): Boolean {
        return false
    }

    override fun onStart(context: LoggerContext?) {
    }

    override fun onReset(context: LoggerContext?) {
    }

    override fun onStop(context: LoggerContext?) {
    }

    override fun onLevelChange(logger: Logger?, level: Level?) {
    }

    override fun start() {
        val propertyResolver = getPropertyResolver()
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

    override fun stop() {
    }

    override fun isStarted(): Boolean {
        return false
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        Companion.environment = environment
    }

    override fun getOrder(): Int {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1
    }

    private fun getPropertyResolver(): PropertyResolver {
        val resolver = PropertySourcesPropertyResolver(
            environment.propertySources
        )
        resolver.conversionService = environment.conversionService
        resolver.setIgnoreUnresolvableNestedPlaceholders(true)
        return resolver
    }

    private fun setFileProperty(resolver: PropertyResolver, loggingPath: String, applicationName: String, logType: LogType) {
        if (logType.fileName != null) {
            val logSuffix = if (logType.fileName.suffix) "-${logType.fileName.aliasName}" else ""
            val loggingFile = resolver.getProperty("${LOGGING_PREFIX}${logType.fileName.aliasName}-file", "$applicationName$logSuffix.log")
            context.putProperty(logType.fileName.fileNameValue, "$loggingPath$loggingFile")
        }
        if (logType.filePattern != null) {
            val filePattern = resolver.getProperty("${LOGGING_PREFIX}file-pattern", logType.filePattern.defaultValue)
            context.putProperty(logType.filePattern.filePatternValue, filePattern)
        }
    }

}

/**
 * 定义不同类型的枚举
 */
enum class LogType (
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
