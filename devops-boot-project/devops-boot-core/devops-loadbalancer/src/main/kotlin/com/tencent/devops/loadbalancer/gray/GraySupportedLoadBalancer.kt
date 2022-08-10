package com.tencent.devops.loadbalancer.gray

import com.tencent.devops.loadbalancer.config.DevOpsLoadBalancerProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.DefaultResponse
import org.springframework.cloud.client.loadbalancer.Request
import org.springframework.cloud.client.loadbalancer.Response
import org.springframework.cloud.client.serviceregistry.Registration
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import reactor.core.publisher.Mono
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger

/**
 * 支持灰度调用的loadbalancer
 * 代码复用RoundRobinLoadBalancer，在此基础上做了增强
 */
class GraySupportedLoadBalancer(
    private val loadBalancerProperties: DevOpsLoadBalancerProperties,
    private val registration: Registration,
    private val serviceInstanceListSupplierProvider: ObjectProvider<ServiceInstanceListSupplier>,
    serviceId: String,
    position: AtomicInteger = AtomicInteger(Random().nextInt(1000))
) : BaseLoadBalancer(serviceInstanceListSupplierProvider, serviceId, position) {
    override fun choose(request: Request<*>?): Mono<Response<ServiceInstance>> {
        val supplier = serviceInstanceListSupplierProvider.getIfAvailable { NoopServiceInstanceListSupplier() }
        return supplier.get(request).next().map { processInstanceResponse(supplier, it) }
    }

    private fun processInstanceResponse(
        supplier: ServiceInstanceListSupplier,
        serviceInstances: List<ServiceInstance>
    ): Response<ServiceInstance> {
        val serviceInstanceResponse = getInstanceResponse(serviceInstances)
        if (supplier is SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            supplier.selectedServiceInstance(serviceInstanceResponse.server)
        }
        return serviceInstanceResponse
    }

    override fun getInstanceResponse(instances: List<ServiceInstance>): Response<ServiceInstance> {
        val filteredInstances = if (loadBalancerProperties.gray.enabled) {
            if (loadBalancerProperties.gray.metaKey.isEmpty()) {
                logger.warn("Load balancer gray meta-key is empty.")
            }
            val localMetaValue = registration.metadata[loadBalancerProperties.gray.metaKey].orEmpty()
            instances.filter { it.metadata[loadBalancerProperties.gray.metaKey].orEmpty() == localMetaValue }
        } else instances

        if (loadBalancerProperties.localPrior.enabled) {
            for (instance in filteredInstances) {
                if (instance.host == registration.host) {
                    return DefaultResponse(instance)
                }
            }
        }

        return roundRobinChoose(filteredInstances)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GraySupportedLoadBalancer::class.java)
    }
}
