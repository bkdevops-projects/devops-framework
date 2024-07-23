package com.tencent.devops.schedule.enums

/**
 * 调度类型
 */
enum class ScheduleTypeEnum(
    private val code: Int,
    private val label: String,
) : DictItem {
    /**
     * 立即执行,调度器不会进行调度，需要主动触发才会执行
     */
    IMMEDIATELY(1, "立即执行"),

    /**
     * 固定时间
     */
    FIX_TIME(2, "固定时间"),

    /**
     * 固定速率
     */
    FIX_RATE(3, "固定速率"),

    /**
     * cron表达式
     */
    CRON(4, "Cron表达式"),
    ;

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): ScheduleTypeEnum? {
            return values().find { it.code == code }
        }
    }
}
