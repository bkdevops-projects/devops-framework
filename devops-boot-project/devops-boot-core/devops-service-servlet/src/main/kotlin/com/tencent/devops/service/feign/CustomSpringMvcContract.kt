package com.tencent.devops.service.feign

import feign.MethodMetadata
import feign.Util.emptyToNull
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation
import org.springframework.core.convert.ConversionService
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 解决feign client不允许@RequestMapping注解的问题
 * */
class CustomSpringMvcContract(
    annotatedParameterProcessors: List<AnnotatedParameterProcessor>,
    conversionService: ConversionService,
    private val decodeSlash: Boolean,
) : SpringMvcContract(annotatedParameterProcessors, conversionService, decodeSlash) {
    private val resourceLoader = DefaultResourceLoader()
    override fun processAnnotationOnClass(data: MethodMetadata, clz: Class<*>) {
        if (clz.interfaces.isEmpty()) {
            val classAnnotation = findMergedAnnotation(clz, RequestMapping::class.java)
            if (classAnnotation != null) {
                // Prepend path from class annotation if specified
                if (classAnnotation.value.isNotEmpty()) {
                    var pathValue = emptyToNull(classAnnotation.value[0])
                    pathValue = resolve(pathValue)
                    if (!pathValue.startsWith("/")) {
                        pathValue = "/$pathValue"
                    }
                    data.template().uri(pathValue)
                    if (data.template().decodeSlash() != decodeSlash) {
                        data.template().decodeSlash(decodeSlash)
                    }
                }
            }
        }
    }

    private fun resolve(value: String): String {
        return if (StringUtils.hasText(value) && resourceLoader is ConfigurableApplicationContext) {
            (resourceLoader as ConfigurableApplicationContext).environment.resolvePlaceholders(value)
        } else {
            value
        }
    }
}
