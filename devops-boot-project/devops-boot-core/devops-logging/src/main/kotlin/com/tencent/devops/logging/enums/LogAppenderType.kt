package com.tencent.devops.logging.enums

/**
 * 定义不同类型的枚举
 */
enum class LogAppenderType(
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
