package com.tencent.devops.schedule.enums

/**
 * worker中台枚举
 */
enum class WorkerStatusEnum(
    private val code: Int,
    private val label: String
): DictItem {

    STOP(0, "停止"),
    RUNNING(1, "正常");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): WorkerStatusEnum? {
            return values().find { it.code == code }
        }
    }
}
