package com.tencent.devops.schedule.enums

enum class TriggerTypeEnum(
    private val code: Int,
    private val label: String
): DictItem {
    MANUAL(0, "手动触发"),
    CRON(1, "CRON触发"),
    RETRY(2, "失败重试"),
    API(3, "API触发"),
    MISFIRE(4, "过期补偿");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): TriggerTypeEnum? {
            return values().find { it.code == code }
        }
    }
}
