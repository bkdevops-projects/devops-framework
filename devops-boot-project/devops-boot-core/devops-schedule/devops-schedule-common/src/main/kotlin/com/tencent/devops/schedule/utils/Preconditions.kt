package com.tencent.devops.schedule.utils

inline fun <R> validate(block: () -> R): R {
    return validate(block) { "Failed requirement." }
}

/**
 * 验证参数执行正确, 顺利则返回验证方法的结果，出现异常则抛[IllegalArgumentException]
 * @param block 验证方法
 * @return R
 */
inline fun <R> validate(block: () -> R, lazyMessage: () -> Any): R {
    try {
        return block()
    } catch (t: Throwable) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    }
}
