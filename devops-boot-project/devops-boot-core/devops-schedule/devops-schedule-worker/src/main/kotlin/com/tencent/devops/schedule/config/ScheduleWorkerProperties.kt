package com.tencent.devops.schedule.config

import com.tencent.devops.schedule.config.ScheduleWorkerProperties.Companion.PREFIX
import com.tencent.devops.schedule.constants.WorkerRegistryMode
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = PREFIX)
data class ScheduleWorkerProperties(
    /**
     * 注册模式
     */
    var mode: WorkerRegistryMode = WorkerRegistryMode.AUTO,
    /**
     * 工作组名称，为空则使用自身的应用名称。仅当mode=AUTO时有效
     */
    var group: String = "",
    /**
     * 自身地址，为空则自动获取。仅当mode=AUTO时有效
     */
    var address: String = "",
    /**
     * 执行器配置
     */
    var executor: ScheduleWorkerExecutorProperties = ScheduleWorkerExecutorProperties(),
    /**
     * 调度中心配置
     */
    var server: ScheduleWorkerServerProperties = ScheduleWorkerServerProperties()
) {
    companion object {
        const val PREFIX = "devops.schedule.worker"
    }

    class ScheduleWorkerExecutorProperties(

        /**
         * 最大线程数
         */
        var maximumPoolSize: Int = 100,

        /**
         * 核心线程数
         */
        var corePoolSize: Int = 1,

        /**
         * 线程存活时间，单位秒
         */
        var keepAliveTime: Long = 60,
    )

    class ScheduleWorkerServerProperties(

        /**
         * 调度中心和执行器之间的accessToken
         */
        var accessToken: String = "secret",

        /**
         * 调度中心地址，DISCOVERY模式下可填写服务名称
         */
        var address: String = "http://localhost:8080"
    )

}
