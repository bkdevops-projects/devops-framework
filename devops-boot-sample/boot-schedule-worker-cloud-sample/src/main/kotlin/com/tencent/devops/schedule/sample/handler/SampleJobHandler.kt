package com.tencent.devops.schedule.sample.handler

import com.tencent.devops.schedule.executor.JobContext
import com.tencent.devops.schedule.executor.JobHandler
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import org.springframework.stereotype.Component

@Component("com.tencent.devops.schedule.sample.SampleJobHandler")
class SampleJobHandler : JobHandler {
    override fun execute(context: JobContext): JobExecutionResult {
        val name = context.jobParamMap["name"].toString()
        println("Hello, $name!")
        return JobExecutionResult.success()
    }
}
