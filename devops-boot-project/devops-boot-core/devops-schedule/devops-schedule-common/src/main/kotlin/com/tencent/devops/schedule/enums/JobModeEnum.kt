package com.tencent.devops.schedule.enums

/**
 * 运行模式
 */
enum class JobModeEnum(
    private val code: Int,
    private val label: String,
    val isContainer: Boolean,
    val isScript: Boolean,
) : DictItem {
    /**
     * Java Bean
     */
    BEAN(1, "Java Bean", false, false),

    /**
     * Shell
     */
    SHELL(2, "Shell", false, true),

    /**
     * K8s shell
     * */
    K8S_SHELL(3, "K8s shell", true, true),
    ;

    override fun code() = code
    override fun description() = label

    companion object {
        const val DEFAULT_IMAGE = "bash"

        /**
         * 根据[code]查找对应的枚举类型
         */
        fun ofCode(code: Int): JobModeEnum? {
            return values().find { it.code == code }
        }
    }
}
