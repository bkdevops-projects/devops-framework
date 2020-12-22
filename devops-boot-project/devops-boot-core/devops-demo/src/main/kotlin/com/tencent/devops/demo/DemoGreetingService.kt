package com.tencent.devops.demo

/**
 * 用户框架演示，Greeting服务接口实现类
 */
class DemoGreetingService(
    private val demoProperties: DemoProperties
) : GreetingService {
    override fun greeting(): String {
        return "Hello ${demoProperties.message}"
    }
}
