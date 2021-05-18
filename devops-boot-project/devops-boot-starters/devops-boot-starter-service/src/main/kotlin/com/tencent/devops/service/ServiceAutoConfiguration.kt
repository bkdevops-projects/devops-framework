package com.tencent.devops.service

import com.tencent.devops.service.feign.FeignFilterRequestMappingHandlerMapping
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

/**
 * Service自动化配置类
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:common-service.properties")
@EnableConfigurationProperties(ServiceProperties::class)
@ConditionalOnWebApplication
@AutoConfigureBefore(WebMvcAutoConfiguration::class)
class ServiceAutoConfiguration {

    @Bean
    fun feignWebRegistrations(): WebMvcRegistrations {
        return object : WebMvcRegistrations {
            override fun getRequestMappingHandlerMapping() = FeignFilterRequestMappingHandlerMapping()
        }
    }
}
