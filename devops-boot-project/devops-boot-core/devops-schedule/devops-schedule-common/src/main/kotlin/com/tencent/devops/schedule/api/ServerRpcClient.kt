package com.tencent.devops.schedule.api

import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam

/**
 * 远程调用客户端
 */
interface ServerRpcClient {

    /**
     * 运行任务
     */
    fun submitResult(result: JobExecutionResult)

    /**
     * 心跳
     */
    fun heartBeat(param: HeartBeatParam)
}
