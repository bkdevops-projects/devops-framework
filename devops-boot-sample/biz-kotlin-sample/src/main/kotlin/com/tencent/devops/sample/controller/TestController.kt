package com.tencent.devops.sample.controller

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.plugin.api.PluginManager
import com.tencent.devops.plugin.api.applyExtension
import com.tencent.devops.sample.client.SampleClient
import com.tencent.devops.sample.config.GreetingProperties
import com.tencent.devops.sample.extension.PrintExtension
import com.tencent.devops.sample.pojo.Sample
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Test Controller
 */
@RestController
class TestController(
    private val pluginManager: PluginManager,
    private val sampleClient: SampleClient,
    private val greetingProperties: GreetingProperties
) {

    @GetMapping("/test")
    fun test(): Response<Sample> {
        return sampleClient.getSample()
    }

    @GetMapping("/greeting")
    fun greeting() = "Hello, ${greetingProperties.message}"

    /**
     * test plugin
     */
    @GetMapping("/print/{content}")
    fun print(@PathVariable content: String) {
        pluginManager.applyExtension<PrintExtension> { print(content) }
    }
}
