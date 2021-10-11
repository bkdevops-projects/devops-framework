package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.pojo.trigger.TriggerParam

/**
 * worker路由类
 */
interface WorkerRouter {
    /**
     * 任务路由
     * @param triggerParam 任务触发参数
     * @param addressList worker地址列表
     *
     * @return 路由地址，可能为null
     */
    fun route(triggerParam: TriggerParam, addressList: List<String>): String?

}
