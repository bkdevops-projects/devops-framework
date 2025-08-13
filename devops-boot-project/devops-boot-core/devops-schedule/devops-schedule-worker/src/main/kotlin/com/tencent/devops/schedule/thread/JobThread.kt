package com.tencent.devops.schedule.thread

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.executor.JobContext
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.utils.jackson.readJsonString
import org.slf4j.LoggerFactory
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 任务线程
 * */
class JobThread(private val serverRpcClient: ServerRpcClient) : Thread() {
    private val triggerLogIdSet = mutableSetOf<String>()
    private val triggerQueue = LinkedBlockingDeque<TriggerTask>()
    private val stop = AtomicBoolean(false)
    private var base64Decoder = Base64.getDecoder()

    /**
     * 待执行任务计数
     * */
    private val pendingTaskCount = ConcurrentHashMap<String, AtomicInteger>()

    /**
     * 取消任务,key为需要取消的任务id，value为设置时队尾的logId
     *
     * 实现在队列中快速的取消任务，如果遍历取消指定job的任务效率会很差，这里通过设置相关标志位，来决定任务是否应该执行
     * */
    private val cancelJobs = ConcurrentHashMap<String, String>()

    init {
        name = "JobThread-${threadId.incrementAndGet()}"
    }

    override fun run() {
        while (!stop.get()) {
            try {
                val task = triggerQueue.poll(3, TimeUnit.SECONDS)
                if (task != null && shouldRun(task)) {
                    val triggerParam = task.param
                    val logId = triggerParam.logId
                    val context = buildJobContext(triggerParam)
                    // 执行任务逻辑，获取结果
                    val result = try {
                        task.jobHandler.execute(context)
                    } catch (e: Throwable) {
                        logger.error("execute job log[$logId] error: ${e.message}", e)
                        JobExecutionResult.failed(e.message.orEmpty())
                    }
                    result.logId = logId
                    logger.info("complete job log[$logId]: $result")
                    // 上报任务结果
                    submitResult(task.jobId, result)
                }
            } catch (e: Exception) {
                if (!stop.get()) {
                    throw e
                }
            }
        }
        while (triggerQueue.size > 0) {
            val task = triggerQueue.poll()
            val result = JobExecutionResult.failed("job not executed, because thread is stopped.")
            result.logId = task.param.logId
            submitResult(task.jobId, result)
        }
    }

    fun pushTriggerQueue(task: TriggerTask): Boolean {
        if (triggerLogIdSet.contains(task.param.logId)) {
            return false
        }
        triggerQueue.add(task)
        triggerLogIdSet.add(task.param.logId)
        pendingTaskCount.getOrPut(task.jobId) { AtomicInteger() }.incrementAndGet()
        return true
    }

    fun toStop() {
        stop.set(true)
    }

    fun removeEarlyJob(jobId: String) {
        triggerQueue.peekLast()?.let {
            cancelJobs[jobId] = it.param.logId
        }
    }

    fun hasRunningJobs(jobId: String): Boolean {
        val count = pendingTaskCount[jobId]?.get() ?: 0
        return count > 0
    }

    private fun shouldRun(task: TriggerTask): Boolean {
        val stopLogId = cancelJobs[task.jobId] ?: return true
        val logId = task.param.logId
        if (logId == stopLogId) {
            cancelJobs.remove(task.jobId)
        }
        val result = JobExecutionResult.failed("cancelled by block strategy")
        result.logId = logId
        submitResult(task.jobId, result)
        return false
    }

    private fun buildJobContext(param: TriggerParam): JobContext {
        with(param) {
            return JobContext(
                jobId = jobId,
                jobParamMap = jobParam.readJsonString(),
                logId = logId,
                triggerTime = triggerTime,
                scheduledFireTime = scheduledFireTime,
                broadcastIndex = broadcastIndex,
                broadcastTotal = broadcastTotal,
                source = if (source != null) String(base64Decoder.decode(param.source)) else null,
                image = if (param.image != null) param.image else null,
                updateTime = param.updateTime,
                command = command,
                cmdFileName = cmdFileName,
            )
        }
    }

    private fun submitResult(jobId: String, result: JobExecutionResult) {
        val logId = result.logId
        try {
            triggerLogIdSet.remove(logId)
            pendingTaskCount[jobId]?.decrementAndGet()
            serverRpcClient.submitResult(result)
            logger.info("submit job log[$logId] result success")
        } catch (e: Exception) {
            logger.error("submit job log[$logId] result error: ${e.message}", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobThread::class.java)
        private val threadId = AtomicLong()
    }
}
