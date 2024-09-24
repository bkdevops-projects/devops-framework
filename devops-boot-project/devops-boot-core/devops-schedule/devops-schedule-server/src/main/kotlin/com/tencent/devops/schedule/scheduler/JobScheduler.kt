package com.tencent.devops.schedule.scheduler

import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.manager.WorkerManager

/**
 * 任务调度器接口
 */
interface JobScheduler {

    /**
     * 启动调度器
     */
    fun start()

    /**
     * 停止调度器
     */
    fun stop()

    /**
     * 触发任务
     * @param jobId 任务id
     * @param triggerType 触发类型
     * @param retryCount 重试次数，可选。不为空时，job参数使用[jobParam]
     * @param jobParam 任务参数，可选。null则使用默认job参数
     * @param shardingParam 分片参数，可选
     */
    fun trigger(
        triggerContext: JobTriggerContext,
        triggerType: TriggerTypeEnum,
        retryCount: Int? = null,
        jobParam: String? = null,
        shardingParam: String? = null,
    )

    /**
     * 触发任务
     * @param jobId 任务id
     * @param triggerType 触发类型
     * @param retryCount 重试次数，可选。不为空时，job参数使用[jobParam]
     * @param jobParam 任务参数，可选。null则使用默认job参数
     * @param shardingParam 分片参数，可选
     */
    fun trigger(
        jobId: String,
        triggerType: TriggerTypeEnum,
        retryCount: Int? = null,
        jobParam: String? = null,
        shardingParam: String? = null,
    )

    /**
     * 获取job manager
     */
    fun getJobManager(): JobManager

    /**
     * 获取worker manager
     */
    fun getWorkerManager(): WorkerManager
}
