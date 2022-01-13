package com.tencent.devops.schedule.api

import com.tencent.devops.schedule.constants.SCHEDULE_RPC_AUTH_HEADER
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RpcAuthRequestInterceptor(
    private val accessToken: String
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val headers = request.headers
        if (!headers.containsKey(SCHEDULE_RPC_AUTH_HEADER)) {
            headers[SCHEDULE_RPC_AUTH_HEADER] = accessToken
        }
        return execution.execute(request, body)
    }
}
