package com.tencent.devops.schedule.thread

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.executor.DefaultJobExecutor
import com.tencent.devops.schedule.executor.JobContext
import com.tencent.devops.schedule.executor.JobHandler
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import com.tencent.devops.utils.jackson.readJsonString
import org.slf4j.LoggerFactory
import java.util.Base64
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 任务线程
 * */
class JobThread(
    val jobId: String,
    private val jobHandler: JobHandler,
    private val serverRpcClient: ServerRpcClient,
) : Thread() {
    private val triggerLogIdSet = mutableSetOf<String>()
    private val triggerQueue = LinkedBlockingQueue<TriggerParam>()
    private val stop = AtomicBoolean(false)
    private var idleTimes = 0
    private var base64Decoder = Base64.getDecoder()
    var running = AtomicBoolean(false)

    init {
        name = "JobThread-$jobId"
    }

    override fun run() {
        logger.info("$name started")
        while (!stop.get()) {
            idleTimes++
            running.set(false)
            try {
                val triggerParam = triggerQueue.poll(3, TimeUnit.SECONDS)
                if (triggerParam != null) {
                    running.set(true)
                    idleTimes = 0
                    val logId = triggerParam.logId
                    triggerLogIdSet.remove(logId)
                    val context = buildJobContext(triggerParam)
                    // 执行任务逻辑，获取结果
                    val result = try {
                        jobHandler.execute(context)
                    } catch (e: Throwable) {
                        logger.error("execute job log[$logId] error: ${e.message}", e)
                        JobExecutionResult.failed(e.message.orEmpty())
                    }
                    result.logId = logId
                    logger.info("complete job log[$logId]: $result")
                    // 上报任务结果
                    try {
                        serverRpcClient.submitResult(result)
                        logger.info("submit job log[$logId] result success")
                    } catch (e: Exception) {
                        logger.error("submit job log[$logId] result error: ${e.message}", e)
                    }
                } else if (idleTimes > 30 && triggerQueue.size == 0) {
                    // 超过最大空闲事件
                    logger.info("executor idle times over limit")
                    DefaultJobExecutor.removeJobThread(jobId)
                }
            } catch (e: Exception) {
                if (!stop.get()) {
                    throw e
                }
            }
        }
        logger.info("$name stopped")
    }

    fun pushTriggerQueue(triggerParam: TriggerParam): Boolean {
        if (triggerLogIdSet.contains(triggerParam.logId)) {
            return false
        }
        triggerQueue.add(triggerParam)
        triggerLogIdSet.add(triggerParam.logId)
        return true
    }

    fun toStop() {
        logger.info("Stopping $name")
        stop.set(true)
        if (running.get()) {
            logger.info("$name is running now,waiting...")
            while (running.get()) {
                // empty
            }
        }
    }

    private fun buildJobContext(param: TriggerParam): JobContext {
        with(param) {
            return JobContext(
                jobId = jobId,
                jobParamMap = jobParam.readJsonString(),
                logId = logId,
                triggerTime = triggerTime,
                broadcastIndex = broadcastIndex,
                broadcastTotal = broadcastTotal,
                source = if (source != null) String(base64Decoder.decode(param.source)) else null,
                image = if (param.image != null) param.image else null,
                updateTime = param.updateTime,
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobThread::class.java)
    }
}
