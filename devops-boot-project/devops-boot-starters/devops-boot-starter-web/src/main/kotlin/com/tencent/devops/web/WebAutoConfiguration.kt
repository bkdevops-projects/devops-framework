package com.tencent.devops.web

import com.tencent.devops.web.banner.DevOpsBannerInitializer
import com.tencent.devops.web.jackson.JacksonConfiguration
import com.tencent.devops.web.swagger.SwaggerConfiguration
import com.tencent.devops.web.util.SpringContextUtils
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource

/**
 * Service自动化配置类
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:common-web.properties")
@Import(
    SpringContextUtils::class,
    JacksonConfiguration::class,
    SwaggerConfiguration::class,
    DevOpsBannerInitializer::class
)
class WebAutoConfiguration
