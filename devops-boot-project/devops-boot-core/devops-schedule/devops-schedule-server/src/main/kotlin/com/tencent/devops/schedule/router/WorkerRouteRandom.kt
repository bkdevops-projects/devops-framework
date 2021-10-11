package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.pojo.trigger.TriggerParam

/**
 * 随机路由
 */
class WorkerRouteRandom : WorkerRouter {

    override fun route(triggerParam: TriggerParam, addressList: List<String>): String {
        return addressList.random()
    }
}
