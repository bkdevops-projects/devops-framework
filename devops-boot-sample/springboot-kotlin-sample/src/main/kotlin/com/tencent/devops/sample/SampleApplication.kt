package com.tencent.devops.sample

import com.tencent.devops.demo.GreetingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 使用DevOpsBoot框架的Sample应用
 */
@SpringBootApplication
@RestController
class SampleApplication {

    @Autowired
    private lateinit var greetingService: GreetingService

    @RequestMapping
    fun greeting() = greetingService.greeting()

}

fun main() {
    runApplication<SampleApplication>()
}