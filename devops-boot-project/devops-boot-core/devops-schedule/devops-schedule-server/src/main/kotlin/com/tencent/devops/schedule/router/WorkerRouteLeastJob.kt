package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.manager.JobManager
import com.tencent.devops.schedule.pojo.trigger.TriggerParam

class WorkerRouteLeastJob(private val jobManager: JobManager) : WorkerRouter {
    override fun route(triggerParam: TriggerParam, addressList: List<String>): String? {
        var selected: String? = null
        var minJobs = Int.MAX_VALUE
        addressList.forEach {
            val countByWorkerAddress = jobManager.countByWorkerAddress(ExecutionCodeEnum.RUNNING.code(), it)
            if (countByWorkerAddress < minJobs) {
                selected = it
                minJobs = countByWorkerAddress
            }
        }
        return selected
    }
}