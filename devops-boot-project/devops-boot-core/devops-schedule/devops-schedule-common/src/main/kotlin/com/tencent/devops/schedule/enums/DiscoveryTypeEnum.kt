package com.tencent.devops.schedule.enums

/**
 * 地址发现策略
 */
enum class DiscoveryTypeEnum(
    private val code: Int,
    private val label: String
): DictItem {

    /**
     * 默认策略
     */
    CLOUD(1, "自动发现(SpringCloud)"),
    AUTO(2, "自动注册"),
    MANUAL(3, "手动注册");

    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): DiscoveryTypeEnum? {
            return values().find { it.code == code }
        }
    }
}
