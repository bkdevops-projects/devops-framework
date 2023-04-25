package com.tencent.devops.webflux.filter

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

/**
 * 协程版HandlerFilterFunction
 * */
interface CoHandlerFilterFunction {

    suspend fun filter(request: ServerRequest, next: suspend (ServerRequest) -> ServerResponse): ServerResponse
}
