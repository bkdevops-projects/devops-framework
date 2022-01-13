package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * 轮训路由
 */
class WorkerRouteRound: WorkerRouter {

    private val routeCountEachJob = ConcurrentHashMap<String, AtomicInteger>()
    private var cacheValidTime = 0L

    override fun route(triggerParam: TriggerParam, addressList: List<String>): String {
        return addressList[count(triggerParam.jobId) % addressList.size]
    }

    private fun count(jobId: String): Int {
        // cache clear
        if (System.currentTimeMillis() > cacheValidTime) {
            routeCountEachJob.clear()
            cacheValidTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24
        }
        var count = routeCountEachJob[jobId]
        if (count == null || count.get() > 1000000) {
            // 初始化时主动Random一次，缓解首次压力
            count = AtomicInteger(Random().nextInt(100))
        } else {
            // count++
            count.addAndGet(1)
        }
        routeCountEachJob[jobId] = count
        return count.get()
    }
}
