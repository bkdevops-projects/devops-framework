package com.tencent.devops.service.feign

import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

/**
 * 重写RequestMappingHandlerMapping的isHandler方法，避免声明Feign Client Api接口的RequestMapping注解
 * 与Feign Client Api实现类的Controller注解重复，造成HandlerMapping以及swagger重复扫描的问题
 */
class FeignFilterRequestMappingHandlerMapping : RequestMappingHandlerMapping() {

    override fun isHandler(beanType: Class<*>): Boolean {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller::class.java) ||
            AnnotatedElementUtils.hasAnnotation(beanType, RestController::class.java)
    }
}
