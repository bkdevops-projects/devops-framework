package com.tencent.devops.sample.controller

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.sample.client.SampleClient
import com.tencent.devops.sample.pojo.Sample
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Sample Controller
 */
@RestController
class SampleController : SampleClient {

    override fun getSample(): Response<Sample> {
        val sample = Sample(
            id = System.currentTimeMillis(),
            name = UUID.randomUUID().toString().replace("-", "")
        )
        return Response.success(sample)
    }
}
