package com.tencent.devops.service

import com.tencent.devops.service.config.ServiceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(ServiceProperties::class)
class ServiceAutoConfiguration
