package com.tencent.devops.service

import com.tencent.devops.service.feign.CustomSpringMvcContract
import com.tencent.devops.service.feign.FeignFilterRequestMappingHandlerMapping
import feign.Contract
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor
import org.springframework.cloud.openfeign.FeignClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.convert.ConversionService

/**
 * Service自动化配置类
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:common-service.properties")
@ConditionalOnWebApplication
@AutoConfigureBefore(WebMvcAutoConfiguration::class)
class ServiceServletAutoConfiguration {

    @Bean
    fun feignWebRegistrations(): WebMvcRegistrations {
        return object : WebMvcRegistrations {
            override fun getRequestMappingHandlerMapping() = FeignFilterRequestMappingHandlerMapping()
        }
    }

    @Bean
    fun feignContract(
        feignClientProperties: FeignClientProperties?,
        parameterProcessors: List<AnnotatedParameterProcessor>,
        feignConversionService: ConversionService,
    ): Contract {
        val decodeSlash = feignClientProperties == null || feignClientProperties.isDecodeSlash
        return CustomSpringMvcContract(parameterProcessors, feignConversionService, decodeSlash)
    }
}
