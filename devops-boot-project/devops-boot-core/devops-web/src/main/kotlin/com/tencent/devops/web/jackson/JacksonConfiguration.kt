package com.tencent.devops.web.jackson

import com.tencent.devops.utils.jackson.JsonUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

/**
 * Jackson 配置类
 */
@Configuration(proxyBeanMethods = false)
class JacksonConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper() = JsonUtils.objectMapper

    @Bean
    @ConditionalOnMissingBean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter(JsonUtils.objectMapper)
    }
}
