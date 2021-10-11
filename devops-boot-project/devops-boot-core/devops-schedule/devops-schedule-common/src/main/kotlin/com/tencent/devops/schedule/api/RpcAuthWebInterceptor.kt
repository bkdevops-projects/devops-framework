package com.tencent.devops.schedule.api

import com.tencent.devops.schedule.constants.SCHEDULE_RPC_AUTH_HEADER
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RpcAuthWebInterceptor(
    private val accessToken: String
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestToken = request.getHeader(SCHEDULE_RPC_AUTH_HEADER)
        if (accessToken != requestToken) {
            logger.warn("Authenticate failed, $SCHEDULE_RPC_AUTH_HEADER[$requestToken] is invalid.")
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RpcAuthWebInterceptor::class.java)
    }
}
