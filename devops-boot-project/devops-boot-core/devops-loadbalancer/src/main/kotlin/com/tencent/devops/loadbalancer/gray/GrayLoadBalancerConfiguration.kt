package com.tencent.devops.loadbalancer.gray

import com.tencent.devops.loadbalancer.config.DevOpsLoadBalancerProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.serviceregistry.Registration
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

/**
 * 支持灰度调用的loadbalancer配置
 */
class GrayLoadBalancerConfiguration : LoadBalancerClientConfiguration() {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Registration::class)
    fun grayReactorServiceInstanceLoadBalancer(
        loadBalancerProperties: DevOpsLoadBalancerProperties,
        registration: Registration,
        environment: Environment,
        loadBalancerClientFactory: LoadBalancerClientFactory
    ): ReactorLoadBalancer<ServiceInstance> {
        val name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME)
        val serviceInstanceListSupplier =
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier::class.java)
        return GraySupportedLoadBalancer(
            loadBalancerProperties = loadBalancerProperties,
            registration = registration,
            serviceInstanceListSupplierProvider = serviceInstanceListSupplier,
            serviceId = name.orEmpty()
        )
    }

    @Bean
    @ConditionalOnMissingBean(Registration::class)
    override fun reactorServiceInstanceLoadBalancer(
        environment: Environment,
        loadBalancerClientFactory: LoadBalancerClientFactory
    ): ReactorLoadBalancer<ServiceInstance> {
        val name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME)
        val serviceInstanceListSupplier =
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier::class.java)
        return BaseLoadBalancer(
            serviceInstanceListSupplierProvider = serviceInstanceListSupplier,
            serviceId = name.orEmpty()
        )
    }
}
