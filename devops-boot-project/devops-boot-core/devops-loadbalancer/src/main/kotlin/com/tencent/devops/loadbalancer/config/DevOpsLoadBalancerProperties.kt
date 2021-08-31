package com.tencent.devops.loadbalancer.config

import com.tencent.devops.loadbalancer.config.DevOpsLoadBalancerProperties.Companion.PREFIX
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 客户端负载均衡配置
 */
@ConfigurationProperties(PREFIX)
data class DevOpsLoadBalancerProperties(
    /**
     * 优先访问本地服务
     */
    var localPrior: LocalPrior = LocalPrior(),

    /**
     * 灰度调用配置
     */
    var gray: Gray = Gray()
) {
    data class Gray(
        /**
         * 是否启用灰度调用
         */
        var enabled: Boolean = false,
        /**
         * 匹配metadata key值
         */
        var metaKey: String = "env"
    )

    data class LocalPrior(
        /**
         * 是否优先访问本地服务
         */
        var enabled: Boolean = false
    )

    companion object {
        const val PREFIX = "devops.loadbalancer"
    }
}
