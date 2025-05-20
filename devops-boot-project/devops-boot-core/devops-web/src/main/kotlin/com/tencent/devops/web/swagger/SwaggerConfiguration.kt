package com.tencent.devops.web.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.utils.Constants.ALL_PATTERN
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
    fun api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .pathsToMatch(ALL_PATTERN)
            .packagesToScan("com.tencent")
            .group("tencent")
            .build()
    }

    @Bean
    fun openApi(): OpenAPI {
        val info = Info()
            .title(applicationName)
            .description(applicationDesc)
            .version(applicationVersion)
        val components = Components().addSecuritySchemes(
            BASIC_AUTH,
            SecurityScheme().type(SecurityScheme.Type.HTTP).name(BASIC_AUTH).description("Basic authorization")
        )
        return OpenAPI().info(info).components(components)
    }

    companion object {
        private const val BASIC_AUTH = "basicAuth"
    }
}
