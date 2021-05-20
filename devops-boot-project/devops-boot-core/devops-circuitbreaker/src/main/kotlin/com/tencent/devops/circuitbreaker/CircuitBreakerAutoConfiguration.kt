package com.tencent.devops.circuitbreaker

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

/**
 * 客户端熔断器自动配置
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:common-circuitbreaker.properties")
class CircuitBreakerAutoConfiguration
