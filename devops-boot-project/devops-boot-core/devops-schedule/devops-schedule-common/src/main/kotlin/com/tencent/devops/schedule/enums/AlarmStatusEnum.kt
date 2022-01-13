package com.tencent.devops.schedule.enums

/**
 * 过期处理策略
 */
enum class AlarmStatusEnum(
    private val code: Int,
    private val label: String
): DictItem {

    LOCKED(-1, "锁定状态"),
    TODO(0, "待处理"),
    IGNORED(1, "无需告警"),
    SUCCESS(2, "告警成功"),
    FAILED(3, "告警失败");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): AlarmStatusEnum? {
            return values().find { it.code == code }
        }
    }
}
