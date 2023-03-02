package com.tencent.devops.sample.config

import com.tencent.devops.sample.filter.SampleHandlerFilterFunction
import com.tencent.devops.sample.handler.SampleHandler
import org.springframework.web.reactive.function.server.coRouter

class RouteConfiguration(
    val sampleHandler: SampleHandler,
    val sampleHandlerFilterFunction: SampleHandlerFilterFunction,
) {

    fun router() = coRouter {
        filter(sampleHandlerFilterFunction::filter)
        GET("/test/{name}", sampleHandler::getSample)
    }
}
