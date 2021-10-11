package com.tencent.devops.schedule.web

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.schedule.config.ScheduleServerProperties
import com.tencent.devops.schedule.constants.SERVER_API_V1
import com.tencent.devops.schedule.constants.SERVER_BASE_PATH
import com.tencent.devops.schedule.enums.AlarmStatusEnum
import com.tencent.devops.schedule.enums.BlockStrategyEnum
import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.enums.JobModeEnum
import com.tencent.devops.schedule.enums.MisfireStrategyEnum
import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.enums.ScheduleTypeEnum
import com.tencent.devops.schedule.enums.TriggerCodeEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.pojo.TokenInfo
import com.tencent.devops.schedule.pojo.UserInfo
import com.tencent.devops.schedule.pojo.UserLoginRequest
import com.tencent.devops.schedule.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$SERVER_BASE_PATH$SERVER_API_V1")
class SystemController(
    scheduleServerProperties: ScheduleServerProperties
) {
    private val authProperties = scheduleServerProperties.auth
    private val signingKey = JwtUtils.createSigningKey(authProperties.secretKey)

    @ResponseBody
    @GetMapping("/dict/{name}")
    fun dict(@PathVariable name: String): Response<*> {
        return Response.success(SYSTEM_DICT[name])
    }

    @PostMapping("/user/login")
    fun login(@RequestBody request: UserLoginRequest): Response<*> {
        return if (tryLogin(request)) {
            val token = JwtUtils.generateToken(signingKey, authProperties.expiration, request.username)
            Response.success(TokenInfo(token))
        } else {
            Response.fail(HttpStatus.UNAUTHORIZED.value(), "认证失败")
        }
    }

    @GetMapping("/user/info")
    fun info(@RequestParam token: String): Response<*> {
        return try {
            val username = JwtUtils.validateToken(signingKey, token).body.subject
            val userInfo = UserInfo(name = username)
            Response.success(userInfo)
        } catch (e: Exception) {
            Response.fail(HttpStatus.BAD_REQUEST.value(), "非法token")
        }
    }

    @PostMapping("/user/logout")
    fun logout(): Response<Void> {
        return Response.success()
    }

    private fun tryLogin(request: UserLoginRequest): Boolean {
        return authProperties.username == request.username && authProperties.password == request.password
    }

    companion object {

        /**
         * 系统字典
         */
        private val SYSTEM_DICT = mapOf<String, Array<*>>(
            "AlarmStatus" to AlarmStatusEnum.values(),
            "BlockStrategy" to BlockStrategyEnum.values(),
            "DiscoveryType" to DiscoveryTypeEnum.values(),
            "ExecutionCode" to ExecutionCodeEnum.values(),
            "JobMode" to JobModeEnum.values(),
            "MisfireStrategy" to MisfireStrategyEnum.values(),
            "RouteStrategy" to RouteStrategyEnum.values(),
            "ScheduleType" to ScheduleTypeEnum.values(),
            "TriggerCode" to TriggerCodeEnum.values(),
            "TriggerStatus" to TriggerStatusEnum.values(),
            "TriggerType" to TriggerTypeEnum.values(),
        )
    }
}
