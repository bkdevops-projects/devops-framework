package com.tencent.devops.sample.filter

import com.tencent.devops.webflux.filter.CoHandlerFilterFunction
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

class SampleHandlerFilterFunction : CoHandlerFilterFunction {
    override suspend fun filter(
        request: ServerRequest,
        next: suspend (ServerRequest) -> ServerResponse,
    ): ServerResponse {
        if (request.pathVariable("name").equals("devops", true)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()
        }
        return next(request)
    }
}
