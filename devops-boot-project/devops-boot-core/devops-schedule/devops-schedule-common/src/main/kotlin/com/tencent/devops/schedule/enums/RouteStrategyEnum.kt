package com.tencent.devops.schedule.enums

/**
 * 路由策略
 */
enum class RouteStrategyEnum(
    private val code: Int,
    private val label: String
): DictItem {

    /**
     * 默认策略
     */
    RANDOM(1, "随机"),
    ROUND(2, "轮训"),
    CONSISTENT(3, "一致性hash"),
    LEAST_JOB(4,"最少任务数"),
    SHARDING_BROADCAST(10, "分片广播");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): RouteStrategyEnum? {
            return values().find { it.code == code }
        }
    }
}
