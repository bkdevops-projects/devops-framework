package com.tencent.devops.demo

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 用于框架演示属性配置
 */
@ConfigurationProperties("devops.demo")
data class DemoProperties(
    var message: String = "world"
)