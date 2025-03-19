package com.tencent.devops.schedule.executor

import com.tencent.devops.schedule.pojo.ScheduleResponse
import com.tencent.devops.schedule.pojo.trigger.TriggerParam

/**
 * 任务执行器接口
 */
interface JobExecutor {
    /**
     * 提交任务
     * @param param 任务触发参数
     */
    fun execute(param: TriggerParam): ScheduleResponse
}
