package com.tencent.devops.sample.client

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.sample.pojo.Sample
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Primary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Sample FeignClient
 */
@Tag(name = "Sample 服务接口")
@Primary
@FeignClient("devops-kotlin-sample", contextId = "SampleClient")
@RequestMapping("/service/sample")
interface SampleClient {

    @Operation(description = "获取Sample")
    @GetMapping
    fun getSample(): Response<Sample>
}
