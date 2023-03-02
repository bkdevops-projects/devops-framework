package com.tencent.devops.service

import com.tencent.devops.service.feign.FeignFilterRequestMappingHandlerMapping
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Service自动化配置类
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@AutoConfigureBefore(WebFluxAutoConfiguration::class)
class ServiceReactiveAutoConfiguration {

    @Bean
    fun feignWebRegistrations(): WebFluxRegistrations {
        return object : WebFluxRegistrations {
            override fun getRequestMappingHandlerMapping() = FeignFilterRequestMappingHandlerMapping()
        }
    }
}
