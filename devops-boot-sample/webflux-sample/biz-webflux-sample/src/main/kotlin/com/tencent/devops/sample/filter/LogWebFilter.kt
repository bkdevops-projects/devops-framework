package com.tencent.devops.sample.filter

import com.tencent.devops.webflux.filter.CoWebFilter
import com.tencent.devops.webflux.filterAndAwait
import org.slf4j.LoggerFactory
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain

class LogWebFilter : CoWebFilter {
    override suspend fun doFilter(exchange: ServerWebExchange, chain: WebFilterChain) {
        val requestPath = exchange.request.uri.path
        logger.info("Request $requestPath in web filter.")
        chain.filterAndAwait(exchange)
        logger.info("Request $requestPath out web filter.")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LogWebFilter::class.java)
    }
}
