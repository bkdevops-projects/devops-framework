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
        const val LOGGING_APP_FILE = "logging.app.file"
        const val LOGGING_ERROR_FILE = "logging.error.file"
        const val LOGGING_FILE_PATTERN = "logging.file.pattern"
        const val DEFAULT_FILE_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS}|%X{ip:--}|%F|%L|%level|%X{err_code:-0}|||||[%t] %m%ex%n"
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
        setAppFile(
            resolver = propertyResolver,
            applicationName = applicationName,
            loggingPath = loggingPath
        )
        setErrorFile(
            resolver = propertyResolver,
            applicationName = applicationName,
            loggingPath = loggingPath
        )
        setFilePattern(propertyResolver)
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

    private fun setAppFile(resolver: PropertyResolver, loggingPath: String, applicationName: String) {
        val appLoggingFile = resolver.getProperty("${LOGGING_PREFIX}file-app", "$applicationName.log")
        context.putProperty(LOGGING_APP_FILE, "$loggingPath$appLoggingFile")
    }

    private fun setErrorFile(resolver: PropertyResolver, loggingPath: String, applicationName: String) {
        val errorLoggingFile = resolver.getProperty("${LOGGING_PREFIX}file-error", "$applicationName-error.log")
        context.putProperty(LOGGING_ERROR_FILE, "$loggingPath$errorLoggingFile")
    }

    private fun setFilePattern(resolver: PropertyResolver) {
        val filePattern = resolver.getProperty("${LOGGING_PREFIX}file-pattern", DEFAULT_FILE_PATTERN)
        context.putProperty(LOGGING_FILE_PATTERN, filePattern)
    }
}
