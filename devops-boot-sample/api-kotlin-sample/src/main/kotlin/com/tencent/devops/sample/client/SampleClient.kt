package com.tencent.devops.sample.client

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.sample.pojo.Sample
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Sample FeignClient
 */
@Api("Sample 服务接口")
@FeignClient("sample", contextId = "SampleClient")
@RequestMapping("/service/sample")
interface SampleClient {

    @ApiOperation("获取Sample")
    @GetMapping
    fun getSample(): Response<Sample>
}
