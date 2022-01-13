package com.tencent.devops.schedule.enums

/**
 * 过期处理策略
 */
enum class MisfireStrategyEnum(
    private val code: Int,
    private val label: String
): DictItem {

    /**
     * 忽略
     */
    IGNORE(1, "忽略"),

    /**
     * 立即补偿执行一次
     */
    RETRY(2, "补偿执行");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): MisfireStrategyEnum? {
            return values().find { it.code == code }
        }
    }
}
