package com.tencent.devops.schedule.enums

/**
 * 任务状态
 */
enum class ExecutionCodeEnum(
    private val code: Int,
    private val label: String
): DictItem {

    INITIALED(0, "未执行"),
    RUNNING(1, "正在执行"),
    SUCCESS(2, "执行成功"),
    FAILED(-1, "执行失败");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): ExecutionCodeEnum? {
            return values().find { it.code == code }
        }
    }
}
