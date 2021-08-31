package com.tencent.devops.web.swagger

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * swagger配置类
 */
@Configuration(proxyBeanMethods = false)
class SwaggerConfiguration {

    @Value("\${spring.application.name:#{null}}")
    private val applicationName: String? = null

    @Value("\${spring.application.desc:#{null}}")
    private val applicationDesc: String? = null

    @Value("\${spring.application.version:#{null}}")
    private val applicationVersion: String? = null

    @Bean
    fun api(): Docket {
        val apiInfo = ApiInfoBuilder()
            .title(applicationName)
            .description(applicationDesc)
            .version(applicationVersion)
            .build()
        return Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController::class.java))
            .paths(PathSelectors.any())
            .build()
    }
}
