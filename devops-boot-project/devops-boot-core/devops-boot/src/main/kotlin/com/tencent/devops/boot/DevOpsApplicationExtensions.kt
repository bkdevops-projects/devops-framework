package com.tencent.devops.boot

import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

/**
 * SpringBoot 提供的runApplication(vararg args: String)方法使用了kotlin的变长参数传递，会导致detekt提示`SpreadOperator`问题
 * 利用数组传递可以避免此问题
 */
inline fun <reified T : Any> runApplication(args: Array<String>): ConfigurableApplicationContext {
    return SpringApplication.run(arrayOf(T::class.java), args)
}
