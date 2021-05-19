package com.tencent.devops.loadbalancer

import com.tencent.devops.loadbalancer.config.DevOpsLoadBalancerProperties
import com.tencent.devops.loadbalancer.gray.GrayLoadBalancerConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients
import org.springframework.context.annotation.Configuration

/**
 * 客户端负载均衡自动配置类
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@EnableConfigurationProperties(DevOpsLoadBalancerProperties::class)
@LoadBalancerClients(defaultConfiguration = [GrayLoadBalancerConfiguration::class])
class LoadBalancerAutoConfiguration
