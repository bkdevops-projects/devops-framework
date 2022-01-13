package com.tencent.devops.schedule.executor

import com.tencent.devops.schedule.pojo.job.JobExecutionResult

/**
 * 任务处理器，调度中心自动调用JobHandler的execute方法
 */
interface JobHandler {
    /**
     * 任务执行逻辑
     * @param context 任务context，可以获取任务参数，任务id，执行记录id等信息
     * @return 任务记录执行结果
     *         JobExecutionResult.success() 执行成功
     *         JobExecutionResult.failed(message) 执行失败
     */
    fun execute(context: JobContext): JobExecutionResult
}
