package com.tencent.devops.schedule.enums

/**
 * 运行模式
 */
enum class TriggerCodeEnum(
    private val code: Int,
    private val label: String
): DictItem {

    INITIALED(0, "未触发"),
    SUCCESS(1, "触发成功"),
    FAILED(-1, "触发失败");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): TriggerCodeEnum? {
            return values().find { it.code == code }
        }
    }
}
