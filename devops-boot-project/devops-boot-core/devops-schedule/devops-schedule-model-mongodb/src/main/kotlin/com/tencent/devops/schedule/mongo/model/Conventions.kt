package com.tencent.devops.schedule.mongo.model

import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.pojo.worker.WorkerInfo

fun TJobInfo.convert(): JobInfo {
    return JobInfo(
        id = id.orEmpty(),
        name = name,
        description = description,
        groupId = groupId,
        createTime = createTime,
        updateTime = updateTime,
        scheduleType = scheduleType,
        scheduleConf = scheduleConf,
        misfireStrategy = misfireStrategy,
        routeStrategy = routeStrategy,
        jobMode = jobMode,
        jobHandler = jobHandler,
        jobParam = jobParam,
        jobTimeout = jobTimeout,
        blockStrategy = blockStrategy,
        maxRetryCount = maxRetryCount,
        triggerStatus = triggerStatus,
        lastTriggerTime = lastTriggerTime,
        nextTriggerTime = nextTriggerTime,
        source = source,
        image = image,
    )
}

fun JobInfo.convert(): TJobInfo {
    return TJobInfo(
        id = id,
        name = name,
        description = description,
        groupId = groupId,
        createTime = createTime,
        updateTime = updateTime,
        scheduleType = scheduleType,
        scheduleConf = scheduleConf,
        misfireStrategy = misfireStrategy,
        routeStrategy = routeStrategy,
        jobMode = jobMode,
        jobHandler = jobHandler,
        jobParam = jobParam,
        jobTimeout = jobTimeout,
        blockStrategy = blockStrategy,
        maxRetryCount = maxRetryCount,
        triggerStatus = triggerStatus,
        lastTriggerTime = lastTriggerTime,
        nextTriggerTime = nextTriggerTime,
        source = source,
        image = image,
    )
}

fun TWorkerGroup.convert(): WorkerGroup {
    return WorkerGroup(
        id = id.orEmpty(),
        name = name,
        discoveryType = discoveryType,
        updateTime = updateTime,
        registryList = addressList.split(","),
    )
}

fun WorkerGroup.convert(): TWorkerGroup {
    return TWorkerGroup(
        id = id,
        name = name,
        discoveryType = discoveryType,
        updateTime = updateTime,
        addressList = registryList.joinToString(","),
    )
}

fun TJobLog.convert(): JobLog {
    return JobLog(
        id = id,
        jobId = jobId,
        groupId = groupId,
        jobHandler = jobHandler,
        jobParam = jobParam,
        workerAddress = workerAddress,
        workerShardingParam = workerShardingParam,
        workerRetryCount = workerRetryCount,
        triggerTime = triggerTime,
        triggerCode = triggerCode,
        triggerMsg = triggerMsg,
        triggerType = triggerType,
        executionTime = executionTime,
        executionCode = executionCode,
        executionMsg = executionMsg,
        alarmStatus = alarmStatus,
    )
}

fun JobLog.convert(): TJobLog {
    return TJobLog(
        id = id,
        jobId = jobId,
        groupId = groupId,
        jobHandler = jobHandler,
        jobParam = jobParam,
        workerAddress = workerAddress,
        workerShardingParam = workerShardingParam,
        workerRetryCount = workerRetryCount,
        triggerTime = triggerTime,
        triggerCode = triggerCode,
        triggerMsg = triggerMsg,
        triggerType = triggerType,
        executionTime = executionTime,
        executionCode = executionCode,
        executionMsg = executionMsg,
        alarmStatus = alarmStatus,
    )
}

fun TWorker.convert(): WorkerInfo {
    return WorkerInfo(
        id = id.orEmpty(),
        address = address,
        group = group,
        updateTime = updateTime,
    )
}

fun WorkerInfo.convert(): TWorker {
    return TWorker(
        id = id.orEmpty(),
        address = address,
        group = group,
        updateTime = updateTime,
    )
}
