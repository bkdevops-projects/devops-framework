package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.pojo.trigger.TriggerParam

/**
 * 路由策略工具类
 */
object Routes {

    private val strategyMap = mapOf(
        RouteStrategyEnum.RANDOM to WorkerRouteRandom(),
        RouteStrategyEnum.ROUND to WorkerRouteRound(),
        RouteStrategyEnum.CONSISTENT to WorkerRouteConsistentHash()
    )

    /**
     * 任务路由
     * @param strategy 路由策略
     * @param triggerParam 任务触发参数
     * @param addressList worker地址列表
     *
     * @return 路由地址，可能为空
     */
    fun route(
        strategy: RouteStrategyEnum,
        triggerParam: TriggerParam,
        addressList: List<String>
    ): String? {
        return strategyMap[strategy]?.route(triggerParam, addressList)
    }
}
