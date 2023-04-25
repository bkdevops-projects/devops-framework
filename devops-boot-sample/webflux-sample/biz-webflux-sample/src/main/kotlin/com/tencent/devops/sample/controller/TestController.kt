package com.tencent.devops.sample.controller

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.sample.client.SampleClient
import com.tencent.devops.sample.config.GreetingProperties
import com.tencent.devops.sample.pojo.Sample
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Test Controller
 */
@RestController
class TestController(
    private val sampleClient: SampleClient,
    private val greetingProperties: GreetingProperties,
) {

    @GetMapping("/test")
    fun test(): Response<Sample> {
        return sampleClient.getSample()
    }

    @GetMapping("/greeting")
    fun greeting() = "Hello, ${greetingProperties.message}"
}
