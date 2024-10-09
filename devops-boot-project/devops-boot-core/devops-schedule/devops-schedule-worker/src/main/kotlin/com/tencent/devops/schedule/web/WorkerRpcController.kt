package com.tencent.devops.schedule.web

import com.tencent.devops.schedule.api.WorkerRpcClient
import com.tencent.devops.schedule.constants.RPC_RUN_JOB
import com.tencent.devops.schedule.constants.WORKER_RPC_V1
import com.tencent.devops.schedule.executor.JobExecutor
import com.tencent.devops.schedule.pojo.ScheduleResponse
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(WORKER_RPC_V1)
class WorkerRpcController(
    private val jobExecutor: JobExecutor
) : WorkerRpcClient {

    @PostMapping(RPC_RUN_JOB)
    override fun runJob(@RequestBody param: TriggerParam): ScheduleResponse {
        return try {
            jobExecutor.execute(param)
        } catch (e: Exception) {
            logger.error("execute job[$param] error: ${e.message}", e)
            ScheduleResponse.failed(e.message.orEmpty())
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorkerRpcController::class.java)
    }
}
