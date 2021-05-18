package com.tencent.devops.web.banner

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class DevOpsBannerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        System.setProperty("devops-boot.version", DevOpsBootVersion.getVersion().orEmpty())
    }
}
