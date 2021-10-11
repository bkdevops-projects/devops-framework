package com.tencent.devops.schedule.config

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.schedule.constants.SCHEDULE_API_AUTH_HEADER
import com.tencent.devops.schedule.utils.JwtUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 调度中心认证拦截器
 */
class ScheduleServerAuthInterceptor(
    authProperties: ScheduleServerProperties.ScheduleServerAuthProperties
) : HandlerInterceptor {

    private val signingKey = JwtUtils.createSigningKey(authProperties.secretKey)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.method == HttpMethod.OPTIONS.name) {
            return true
        }
        val jwtToken = request.getHeader(SCHEDULE_API_AUTH_HEADER)
        return try {
            JwtUtils.validateToken(signingKey, jwtToken).body.subject
            true
        } catch (exception: Exception) {
            logger.warn("Authenticate failed, $SCHEDULE_API_AUTH_HEADER[$jwtToken] is invalid.")
            response.contentType = APPLICATION_JSON_VALUE
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.println(Response.fail(HttpStatus.UNAUTHORIZED.value(), "认证失败"))
            false
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ScheduleServerAuthInterceptor::class.java)
    }
}
