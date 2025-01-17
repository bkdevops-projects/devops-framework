package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.pojo.trigger.TriggerParam

/**
 * 路由策略工具类
 */
class Routes(jobManager: JobManager) {

    init {
        Companion.jobManager = jobManager
    }

    companion object {
        private lateinit var jobManager: JobManager
        private val strategyMap by lazy {
            mapOf(
                RouteStrategyEnum.RANDOM to WorkerRouteRandom(),
                RouteStrategyEnum.ROUND to WorkerRouteRound(),
                RouteStrategyEnum.CONSISTENT to WorkerRouteConsistentHash(),
                RouteStrategyEnum.LEAST_JOB to WorkerRouteLeastJob(jobManager),
            )
        }

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
}
