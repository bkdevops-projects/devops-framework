package com.tencent.devops.loadbalancer.gray

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.DefaultResponse
import org.springframework.cloud.client.loadbalancer.EmptyResponse
import org.springframework.cloud.client.loadbalancer.Request
import org.springframework.cloud.client.loadbalancer.Response
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import reactor.core.publisher.Mono
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

open class BaseLoadBalancer(
    private val serviceInstanceListSupplierProvider: ObjectProvider<ServiceInstanceListSupplier>,
    private val serviceId: String,
    private val position: AtomicInteger = AtomicInteger(Random().nextInt(1000))
) : ReactorServiceInstanceLoadBalancer {
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

    protected open fun getInstanceResponse(instances: List<ServiceInstance>): Response<ServiceInstance> {
        // 使用k8s的service的时候，应该只有一个endpoint，所以这里只取第一个
        if (instances.size == 1) {
            return DefaultResponse(instances[0])
        }
        return roundRobinChoose(instances)
    }

    protected open fun roundRobinChoose(instances: List<ServiceInstance>): Response<ServiceInstance> {
        if (instances.isEmpty()) {
            if (logger.isWarnEnabled) {
                logger.warn("No servers available for service: $serviceId")
            }
            return EmptyResponse()
        }

        // TODO: enforce order?
        val pos = abs(this.position.incrementAndGet())
        val instance = instances[pos % instances.size]
        return DefaultResponse(instance)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BaseLoadBalancer::class.java)
    }
}
