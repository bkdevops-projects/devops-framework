package com.tencent.devops.schedule.enums

/**
 * 阻塞处理策略
 */
enum class BlockStrategyEnum(
    private val code: Int,
    private val label: String
): DictItem {

    DEFAULT(1, "默认策略");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): BlockStrategyEnum? {
            return values().find { it.code == code }
        }
    }
}
