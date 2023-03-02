package com.tencent.devops.sample.handler

import com.tencent.devops.api.pojo.Response
import com.tencent.devops.sample.pojo.Sample
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

class SampleHandler {
    suspend fun getSample(request: ServerRequest): ServerResponse {
        val name = request.pathVariable("name")
        val sample = Sample(
            id = System.currentTimeMillis(),
            name = name,
        )
        return ok().bodyValueAndAwait(Response.success(sample))
    }
}
