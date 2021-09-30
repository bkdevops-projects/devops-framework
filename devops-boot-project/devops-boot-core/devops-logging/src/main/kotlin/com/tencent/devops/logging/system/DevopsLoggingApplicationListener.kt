package com.tencent.devops.logging.system

import com.tencent.devops.logging.system.logback.DevopsLogbackLoggingSystem
import org.springframework.boot.context.event.ApplicationStartingEvent
import org.springframework.boot.logging.LoggingSystem
import org.springframework.context.ApplicationListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class DevopsLoggingApplicationListener : ApplicationListener<ApplicationStartingEvent> {
    /**
     * logging system由系统变量确定
     * 判断，如果没有配置系统变量，则设置默认值
     * todo，后续可以使用代理的设计模式，将3种日志框架统一起来
     */
    override fun onApplicationEvent(event: ApplicationStartingEvent) {
        val loggingSystem = System.getProperty(LoggingSystem.SYSTEM_PROPERTY)
        if (loggingSystem.isNullOrBlank()) {
            System.setProperty(LoggingSystem.SYSTEM_PROPERTY, DevopsLogbackLoggingSystem::class.java.name)
        }
    }
}
