package com.tencent.devops.schedule.web

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.constants.RPC_HEART_BEAT
import com.tencent.devops.schedule.constants.RPC_SUBMIT_RESULT
import com.tencent.devops.schedule.constants.SERVER_BASE_PATH
import com.tencent.devops.schedule.constants.SERVER_RPC_V1
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.manager.WorkerManager
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$SERVER_BASE_PATH$SERVER_RPC_V1")
class ServerRpcController(
    private val jobManager: JobManager,
    private val workerManager: WorkerManager
) : ServerRpcClient {

    @PostMapping(RPC_SUBMIT_RESULT)
    override fun submitResult(@RequestBody result: JobExecutionResult) {
        with(result) {
            jobManager.completeJob(logId.orEmpty(), code, message)
        }
    }

    @PostMapping(RPC_HEART_BEAT)
    override fun heartBeat(@RequestBody param: HeartBeatParam) {
        workerManager.workerHeartbeat(param)
    }
}
