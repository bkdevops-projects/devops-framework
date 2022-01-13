package com.tencent.devops.schedule.remote

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.config.ScheduleWorkerProperties
import com.tencent.devops.schedule.constants.RPC_HEART_BEAT
import com.tencent.devops.schedule.constants.RPC_SUBMIT_RESULT
import com.tencent.devops.schedule.constants.SERVER_RPC_V1
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject

class RemoteServerRpcClient(
    workerProperties: ScheduleWorkerProperties,
    private val workerRestTemplate: RestTemplate,
) : ServerRpcClient {

    private val serverUrl = normalizeUrl(workerProperties.server)

    override fun submitResult(result: JobExecutionResult) {
        workerRestTemplate.postForObject<Void?>(serverUrl + RPC_SUBMIT_RESULT, result)
    }

    override fun heartBeat(param: HeartBeatParam) {
        workerRestTemplate.postForObject<Void?>(serverUrl + RPC_HEART_BEAT, param)
    }

    private fun normalizeUrl(server: ScheduleWorkerProperties.ScheduleWorkerServerProperties): String {
        val base = server.address.trim().trimEnd('/')
        return "$base$SERVER_RPC_V1"
    }
}
