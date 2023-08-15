package com.tencent.devops.web.swagger

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ReflectionUtils
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.HttpAuthenticationScheme
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider
import kotlin.streams.toList

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
            .securityContexts(securityContexts())
            .securitySchemes(securitySchemes())
    }

    private fun securitySchemes(): List<SecurityScheme> {
        val basicAuthScheme = HttpAuthenticationScheme.BASIC_AUTH_BUILDER
            .name(BASIC_AUTH)
            .description("Basic authorization")
            .build()
        return listOf(basicAuthScheme)
    }

    private fun securityContexts(): List<SecurityContext> {
        val securityContext = SecurityContext.builder()
            .securityReferences(basicAuthReference())
            .operationSelector { true }
            .build()
        return listOf(securityContext)
    }

    private fun basicAuthReference(): List<SecurityReference> {
        val basicSecurityReference = SecurityReference(BASIC_AUTH, emptyArray())
        return listOf(basicSecurityReference)
    }

    /**
     * 处理升级springboot,导致的swagger启动报错
     * @see <a href="https://github.com/springfox/springfox/issues/3462">bug in springfox</a>
     * */
    @Bean
    fun springfoxHandlerProviderBeanPostProcessor(): BeanPostProcessor {
        return object : BeanPostProcessor {
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
                if (bean is WebMvcRequestHandlerProvider || bean is WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean))
                }
                return bean
            }

            private fun customizeSpringfoxHandlerMappings(mappings: MutableList<Any>) {
                val copy = mappings.stream()
                    .filter { mapping ->
                        val field = ReflectionUtils.findField(mapping.javaClass, "patternParser")
                        field?.isAccessible = true
                        field?.get(mapping) == null
                    }.toList()
                mappings.clear()
                mappings.addAll(copy)
            }

            @Suppress("UNCHECKED_CAST")
            private fun getHandlerMappings(bean: Any): MutableList<Any> {
                val field = ReflectionUtils.findField(bean.javaClass, "handlerMappings") ?: return mutableListOf()
                field.isAccessible = true
                return field.get(bean) as MutableList<Any>
            }
        }
    }

    companion object {
        private const val BASIC_AUTH = "basicAuth"
    }
}
