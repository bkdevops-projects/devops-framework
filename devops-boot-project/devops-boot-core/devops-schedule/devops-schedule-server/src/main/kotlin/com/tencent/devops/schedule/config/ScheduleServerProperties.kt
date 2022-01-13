package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.config.ScheduleServerProperties.Companion.PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * 调度中心配置
 */
@ConfigurationProperties(prefix = PREFIX)
data class ScheduleServerProperties(
    /**
     * 是否开启调度中心
     */
    var enabled: Boolean = true,

    /**
     * context path
     */
    var contextPath: String = "/",

    /**
     * 任务触发线程池大小
     */
    var maxTriggerPoolSize: Int = 100,

    /**
     * UI配置
     */
    var ui: ScheduleServerUiProperties = ScheduleServerUiProperties(),

    /**
     * 认证配置
     */
    var auth: ScheduleServerAuthProperties = ScheduleServerAuthProperties()
) {
    companion object {
        const val PREFIX = "devops.schedule.server"
    }

    class ScheduleServerUiProperties(
        /**
         * 是否开启ui界面
         */
        var enabled: Boolean = true
    )

    class ScheduleServerAuthProperties(
        /**
         * 调度中心和执行器之间的accessToken
         */
        var accessToken: String = "secret",

        /**
         * 前端ui访问用户名
         */
        var username: String = "admin",

        /**
         * 前端ui访问密码
         */
        var password: String = "password",

        /**
         * 加密密钥
         */
        var secretKey: String = "secret",

        /**
         * 过期时间
         */
        var expiration: Duration = Duration.ZERO,
    )
}
