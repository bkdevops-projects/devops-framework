package com.tencent.devops.webflux.filter

import kotlinx.coroutines.reactor.mono
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * 协程版WebFilter
 * */
interface CoWebFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            doFilter(exchange, chain)
        }.then()
    }

    suspend fun doFilter(exchange: ServerWebExchange, chain: WebFilterChain)
}
