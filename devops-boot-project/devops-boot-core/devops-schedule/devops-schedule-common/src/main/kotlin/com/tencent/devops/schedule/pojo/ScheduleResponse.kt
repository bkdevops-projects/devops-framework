package com.tencent.devops.schedule.pojo

import com.tencent.devops.schedule.enums.TriggerCodeEnum

/**
 * 调度结果
 */
data class ScheduleResponse(
    val code: Int,
    val message: String? = null
) {
    companion object {

        /**
         * 执行成功
         */
        fun success() = ScheduleResponse(TriggerCodeEnum.SUCCESS.code())

        /**
         * 执行失败
         */
        fun failed(message: String) = ScheduleResponse(TriggerCodeEnum.FAILED.code(), message)
    }
}

