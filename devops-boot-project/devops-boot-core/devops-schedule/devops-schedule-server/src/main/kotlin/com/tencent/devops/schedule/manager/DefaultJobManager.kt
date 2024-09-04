package com.tencent.devops.schedule.manager

import com.tencent.devops.schedule.constants.DATE_TIME_FORMATTER
import com.tencent.devops.schedule.constants.MAX_LOG_MESSAGE_SIZE
import com.tencent.devops.schedule.constants.PRE_LOAD_TIME
import com.tencent.devops.schedule.enums.BlockStrategyEnum
import com.tencent.devops.schedule.enums.JobModeEnum
import com.tencent.devops.schedule.enums.JobModeEnum.Companion.DEFAULT_IMAGE
import com.tencent.devops.schedule.enums.MisfireStrategyEnum
import com.tencent.devops.schedule.enums.RouteStrategyEnum
import com.tencent.devops.schedule.enums.ScheduleTypeEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.enums.TriggerTypeEnum
import com.tencent.devops.schedule.pojo.job.JobCreateRequest
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.job.JobQueryParam
import com.tencent.devops.schedule.pojo.job.JobUpdateRequest
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.log.LogQueryParam
import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.provider.JobProvider
import com.tencent.devops.schedule.provider.WorkerProvider
import com.tencent.devops.schedule.scheduler.JobScheduler
import com.tencent.devops.schedule.utils.computeNextTriggerTime
import com.tencent.devops.schedule.utils.validate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.BasicJsonParser
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.support.CronExpression
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * JobManager默认实现类
 */
open class DefaultJobManager(
    private val jobProvider: JobProvider,
    private val workerProvider: WorkerProvider,
) : JobManager {

    private val jsonParser = BasicJsonParser()

    @Lazy
    @Autowired
    private lateinit var jobScheduler: JobScheduler

    override fun listJobPage(param: JobQueryParam): Page<JobInfo> {
        return jobProvider.listJobPage(param)
    }

    override fun findTodoJobs(time: Long, limit: Int): List<JobInfo> {
        return jobProvider.findTodoJobs(time, limit)
    }

    override fun listLogPage(param: LogQueryParam): Page<JobLog> {
        if (!param.triggerTime.isNullOrEmpty()) {
            try {
                param.triggerTimeFrom = LocalDateTime.parse(param.triggerTime!![0], DATE_TIME_FORMATTER)
                if (param.triggerTime!!.size > 1) {
                    param.triggerTimeTo = LocalDateTime.parse(param.triggerTime!![1], DATE_TIME_FORMATTER)
                }
            } catch (e: Exception) {
                throw RuntimeException("triggerTime格式不正确")
            }
        }
        return jobProvider.listLogPage(param)
    }

    override fun createJob(request: JobCreateRequest): String {
        with(request) {
            // 验证参数
            require(name.isNotBlank())
            // 验证调度参数
            val scheduleTypeEnum = ScheduleTypeEnum.ofCode(scheduleType)
            requireNotNull(scheduleTypeEnum)
            validateScheduleParameter(scheduleConf, scheduleTypeEnum)
            // 验证job handler
            if (request.jobParam.isNullOrBlank()) {
                request.jobParam = "{}"
            }
            validate { jsonParser.parseMap(request.jobParam) }

            // 验证路由策略
            requireNotNull(RouteStrategyEnum.ofCode(routeStrategy))
            // 验证过期策略
            requireNotNull(MisfireStrategyEnum.ofCode(misfireStrategy))
            // 验证阻塞策略
            requireNotNull(BlockStrategyEnum.ofCode(blockStrategy))
            // 验证group存在
            val workerGroup = workerProvider.findGroupById(groupId)
            requireNotNull(workerGroup)

            // 验证任务类型
            val jobModeEnum = JobModeEnum.ofCode(jobMode)
            requireNotNull(jobModeEnum)
            val finalImage = if (jobModeEnum.isContainer && image == null) DEFAULT_IMAGE else image
            if (jobModeEnum == JobModeEnum.BEAN) {
                require(request.jobHandler.isNotBlank())
            }

            val jobInfo = JobInfo(
                name = name,
                description = description,
                groupId = groupId,
                scheduleType = scheduleType,
                scheduleConf = scheduleConf,
                misfireStrategy = misfireStrategy,
                routeStrategy = routeStrategy,
                blockStrategy = blockStrategy,
                jobMode = jobMode,
                jobHandler = jobHandler,
                jobParam = jobParam.orEmpty(),
                jobTimeout = jobTimeout,
                maxRetryCount = maxRetryCount,
                lastTriggerTime = 0,
                nextTriggerTime = 0,
                triggerStatus = TriggerStatusEnum.STOP.code(),
                createTime = LocalDateTime.now(),
                updateTime = LocalDateTime.now(),
                source = source,
                image = finalImage,
            )

            // 一次性任务，不主动触发
            if (scheduleTypeEnum != ScheduleTypeEnum.IMMEDIATELY) {
                // 生成下次执行时间
                val from = LocalDateTime.now().plus(PRE_LOAD_TIME, ChronoUnit.MILLIS)
                val nextTriggerTime = computeNextTriggerTime(jobInfo, from)
                requireNotNull(nextTriggerTime)
                jobInfo.nextTriggerTime = nextTriggerTime
            }

            return jobProvider.addJob(jobInfo).also {
                jobInfo.id = it
                logger.info("create job[$it] success")
            }
        }
    }

    override fun updateJob(request: JobUpdateRequest) {
        // 查询是否存在
        val jobInfo = jobProvider.findJobById(request.id)
        requireNotNull(jobInfo)

        with(request) {
            // 验证调度参数
            scheduleType?.let {
                val scheduleTypeEnum = ScheduleTypeEnum.ofCode(it)
                requireNotNull(scheduleTypeEnum)
                validateScheduleParameter(scheduleConf.orEmpty(), scheduleTypeEnum)
                jobInfo.scheduleType = it
                jobInfo.scheduleConf = scheduleConf.orEmpty()
            }
            description?.let {
                jobInfo.description = description
            }
            groupId?.let {
                if (jobInfo.groupId != it) {
                    requireNotNull(workerProvider.findGroupById(it))
                    jobInfo.groupId = it
                }
            }
            if (!jobParam.isNullOrBlank()) {
                validate { jsonParser.parseMap(jobParam) }
                jobInfo.jobParam = jobParam!!
            }
            jobHandler?.let {
                jobInfo.jobHandler = it
            }
            // 验证路由策略
            routeStrategy?.let {
                requireNotNull(RouteStrategyEnum.ofCode(it))
                jobInfo.routeStrategy = it
            }
            // 验证过期策略
            misfireStrategy?.let {
                requireNotNull(MisfireStrategyEnum.ofCode(it))
                jobInfo.misfireStrategy = it
            }
            // 验证阻塞策略
            blockStrategy?.let {
                requireNotNull(BlockStrategyEnum.ofCode(it))
                jobInfo.blockStrategy = it
            }
            image?.let {
                jobInfo.image = image
            }
            source?.let {
                jobInfo.source = it
            }
            maxRetryCount?.let {
                jobInfo.maxRetryCount = it
            }
            jobInfo.updateTime = LocalDateTime.now()
            jobProvider.updateJob(jobInfo).also {
                logger.info("update job[${jobInfo.id}] success")
            }
        }
    }

    override fun startJob(id: String) {
        val job = jobProvider.findJobById(id)
        requireNotNull(job)
        if (job.scheduleType == ScheduleTypeEnum.IMMEDIATELY.code()) {
            throw IllegalArgumentException("IMMEDIATELY schedule type limit start.")
        }
        // 生成下次执行时间, 延后一段时间执行，避开预读周期
        val from = LocalDateTime.now().plus(PRE_LOAD_TIME, ChronoUnit.MILLIS)
        val nextTriggerTime = computeNextTriggerTime(job, from)
        requireNotNull(nextTriggerTime)
        job.lastTriggerTime = 0
        job.nextTriggerTime = nextTriggerTime
        job.triggerStatus = TriggerStatusEnum.RUNNING.code()

        job.updateTime = LocalDateTime.now()
        jobProvider.updateJob(job)
        logger.info("start job[$id] success")
    }

    override fun stopJob(id: String) {
        val job = jobProvider.findJobById(id)
        requireNotNull(job)
        job.lastTriggerTime = 0
        job.nextTriggerTime = 0
        job.triggerStatus = TriggerStatusEnum.STOP.code()
        job.updateTime = LocalDateTime.now()
        jobProvider.updateJob(job)
        logger.info("stop job[$id] success")
    }

    override fun deleteJob(id: String) {
        val job = jobProvider.findJobById(id)
        if (job != null) {
            jobProvider.deleteJobById(id)
            jobProvider.deleteLogByJobId(id)
        }
        logger.info("delete job[$id] success")
    }

    override fun triggerJob(id: String, executorParam: String?) {
        val job = jobProvider.findJobById(id)
        requireNotNull(job)
        jobScheduler.trigger(job.id.orEmpty(), TriggerTypeEnum.MANUAL, jobParam = executorParam)
    }

    override fun updateJobSchedule(job: JobInfo) {
        jobProvider.updateJobSchedule(job)
        logger.debug("update job schedule[${job.id}] success")
    }

    override fun findJobById(id: String): JobInfo? {
        return jobProvider.findJobById(id)
    }

    override fun addJobLog(jobLog: JobLog): String {
        return jobProvider.addJobLog(jobLog).apply {
            jobLog.id = this
        }
    }

    override fun updateJobLog(jobLog: JobLog) {
        jobProvider.updateJobLog(jobLog)
        logger.debug("update job log[${jobLog.id}] success")
    }

    override fun updateAlarmStatus(logId: String, old: Int, new: Int): Int {
        return jobProvider.updateAlarmStatus(logId, old, new)
    }

    override fun findJobLogById(id: String): JobLog? {
        return jobProvider.findJobLogById(id)
    }

    override fun completeJob(logId: String, executionCode: Int, executionMessage: String?) {
        var finalExecutionMessage = executionMessage.orEmpty()
        if (finalExecutionMessage.length > MAX_LOG_MESSAGE_SIZE) {
            finalExecutionMessage = finalExecutionMessage.substring(0, MAX_LOG_MESSAGE_SIZE)
        }
        jobProvider.updateExecutionResult(logId, executionCode, finalExecutionMessage, LocalDateTime.now())
        logger.debug("complete job log[$logId] success")
    }

    override fun findLostJobLogIds(triggerTime: LocalDateTime): List<String> {
        return jobProvider.findLostJobLogIds(triggerTime)
    }

    override fun findFailJobLogIds(limit: Int): List<String> {
        return jobProvider.findFailJobLogIds(limit)
    }

    /**
     * 验证调度参数
     */
    private fun validateScheduleParameter(scheduleConf: String, scheduleTypeEnum: ScheduleTypeEnum) {
        when (scheduleTypeEnum) {
            ScheduleTypeEnum.IMMEDIATELY -> return
            ScheduleTypeEnum.FIX_TIME -> {
                val triggerTime = validate { LocalDateTime.parse(scheduleConf, DATE_TIME_FORMATTER) }
                require(triggerTime.isAfter(LocalDateTime.now()))
            }

            ScheduleTypeEnum.FIX_RATE -> {
                validate { scheduleConf.toLong() > 0 }
            }

            ScheduleTypeEnum.CRON -> {
                validate { CronExpression.parse(scheduleConf) }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultJobManager::class.java)
    }
}
