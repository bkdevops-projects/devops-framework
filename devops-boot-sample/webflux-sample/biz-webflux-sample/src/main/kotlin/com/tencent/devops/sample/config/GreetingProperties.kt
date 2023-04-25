package com.tencent.devops.sample.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("greeting")
data class GreetingProperties(
    var message: String = "default",
)
