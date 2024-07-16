package com.tencent.devops.schedule.handler

import com.tencent.devops.common.time.toEpochMilli
import com.tencent.devops.schedule.executor.JobContext
import com.tencent.devops.schedule.executor.JobHandler
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.utils.jackson.toJsonString
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread

/**
 * 处理脚本任务
 * 在主机本地生成脚本，并执行。
 * */
class ShellHandler(private val path: String) : JobHandler {
    override fun execute(context: JobContext): JobExecutionResult {
        // 生成脚本文件
        with(context) {
            requireNotNull(source)
            logger.info("Prepare script")
            val scriptFileName = "${jobId}_${updateTime.toEpochMilli()}.sh"
            val scriptFilePath = Paths.get(path, scriptFileName)
            // 创建脚本文件
            if (!Files.exists(scriptFilePath)) {
                if (!Files.exists(scriptFilePath.parent)) {
                    Files.createDirectories(scriptFilePath.parent)
                }
                Files.createFile(scriptFilePath)
                scriptFilePath.toFile().writeText(source!!)
                logger.info("Generate script file $scriptFilePath")
            } else {
                logger.info("Find file $scriptFilePath")
            }
            // 运行脚本
            logger.info("Execute script")
            val pb = ProcessBuilder(BASH_CMD, scriptFilePath.toString())
            setEnv(pb, context)
            pb.redirectErrorStream(true)
            val process = pb.start()
            thread {
                showLog(process.inputStream)
            }
            val exitValue = process.waitFor()
            return if (exitValue == 0) {
                JobExecutionResult.success()
            } else {
                JobExecutionResult.failed("script exit value($exitValue) is failed")
            }
        }
    }

    private fun showLog(inputStream: InputStream, error: Boolean = false) {
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        reader.use {
            var line = it.readLine()
            while (line != null) {
                if (error) {
                    logger.error(line)
                } else {
                    logger.info(line)
                }
                line = it.readLine()
            }
        }
    }

    private fun setEnv(pb: ProcessBuilder, context: JobContext) {
        pb.environment()[JobSystemEnv.JOB_ID] = context.jobId
        pb.environment()[JobSystemEnv.JOB_PARAMETERS] = context.jobParamMap.toJsonString()
        pb.environment()[JobSystemEnv.LOG_ID] = context.logId
        pb.environment()[JobSystemEnv.TRIGGER_TIME] = context.triggerTime.toEpochMilli().toString()
        pb.environment()[JobSystemEnv.BROADCAST_INDEX] = context.broadcastIndex.toString()
        pb.environment()[JobSystemEnv.BROADCAST_TOTAL] = context.broadcastTotal.toString()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ShellHandler::class.java)
        const val BASH_CMD = "bash"
    }
}
