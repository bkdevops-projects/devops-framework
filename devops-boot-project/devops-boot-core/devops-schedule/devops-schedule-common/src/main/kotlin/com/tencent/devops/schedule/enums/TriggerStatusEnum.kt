package com.tencent.devops.schedule.enums

/**
 * 任务状态
 */
enum class TriggerStatusEnum(
    private val code: Int,
    private val label: String
): DictItem {
    /**
     * 停止
     */
    STOP(0, "停止"),

    /**
     * 运行
     */
    RUNNING(1, "运行");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): TriggerStatusEnum? {
            return values().find { it.code == code }
        }
    }
}
