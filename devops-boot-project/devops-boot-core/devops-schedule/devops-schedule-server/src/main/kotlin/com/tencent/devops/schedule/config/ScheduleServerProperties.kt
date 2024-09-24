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
     * 任务触发线程池队列大小
     */
    var maxTriggerQueueSize: Int = 1000,

    /**
     * 调度延迟SLO，单位s
     * */
    var slo: List<Int> = listOf(5),

    /**
     * 期望的最大的调度延迟时间
     * 当实际调度延迟大于设置阈值时，则会降低调度吞吐量，以达到期望的调度延迟
     * */
    var maxScheduleLatencyMillis: Long = Long.MAX_VALUE,

    /**
     * UI配置
     */
    var ui: ScheduleServerUiProperties = ScheduleServerUiProperties(),

    /**
     * 认证配置
     */
    var auth: ScheduleServerAuthProperties = ScheduleServerAuthProperties(),
) {
    companion object {
        const val PREFIX = "devops.schedule.server"
    }

    class ScheduleServerUiProperties(
        /**
         * 是否开启ui界面
         */
        var enabled: Boolean = true,
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
