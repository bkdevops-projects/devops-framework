package com.tencent.devops.schedule.enums

/**
 * 运行模式
 */
enum class JobModeEnum(
    private val code: Int,
    private val label: String
): DictItem {
    /**
     * Java Bean
     */
    BEAN(1, "Java Bean"),

    /**
     * Shell
     */
    SHELL(2, "Shell");


    override fun code() = code
    override fun description() = label

    companion object {
        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): JobModeEnum? {
            return values().find { it.code == code }
        }
    }
}
