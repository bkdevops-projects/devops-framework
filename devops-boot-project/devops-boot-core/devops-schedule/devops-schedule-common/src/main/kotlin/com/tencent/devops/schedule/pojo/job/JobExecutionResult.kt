package com.tencent.devops.schedule.pojo.job

import com.tencent.devops.schedule.enums.ExecutionCodeEnum

data class JobExecutionResult(
    /**
     * 状态码，参考[ExecutionCodeEnum]
     */
    val code: Int,
    /**
     * 备注信息（失败原因、任务说明等，用于查询和展示）
     */
    val message: String? = null
) {
    /**
     * 任务执行记录id，由系统自动填充
     */
    var logId: String? = null

    companion object {

        /**
         * 执行成功
         */
        fun success() = JobExecutionResult(ExecutionCodeEnum.SUCCESS.code())

        /**
         * 执行失败
         * @param message 备注信息（失败原因、任务说明等，用于查询和展示）
         */
        fun failed(message: String) = JobExecutionResult(ExecutionCodeEnum.FAILED.code(), message)
    }
}
