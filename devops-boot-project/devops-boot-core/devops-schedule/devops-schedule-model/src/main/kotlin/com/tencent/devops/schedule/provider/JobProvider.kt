package com.tencent.devops.schedule.provider

import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.job.JobQueryParam
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.log.LogQueryParam
import com.tencent.devops.schedule.pojo.page.Page
import java.time.LocalDateTime

/**
 * Job Provider
 */
interface JobProvider {

    /**
     * 创建任务
     * @param jobInfo 任务信息
     * @return 主键id
     */
    fun addJob(jobInfo: JobInfo): String

    /**
     * 更新job信息
     * @param job job信息
     */
    fun updateJob(job: JobInfo)

    /**
     * 更新job调度信息
     * 只更新调度状态、上次调度时间、下次调度时间
     * @param job job信息
     */
    fun updateJobSchedule(job: JobInfo)

    /**
     * 分页查询job
     * @param param 查询参数
     */
    fun listJobPage(param: JobQueryParam): Page<JobInfo>

    /**
     * 根据下次触发时间查询需要执行的任务
     * @param time 下次触发时间
     * @param limit 返回限制数量
     */
    fun findTodoJobs(time: Long, limit: Int): List<JobInfo>

    /**
     * 根据id查找任务
     * @param id 任务id
     */
    fun findJobById(id: String): JobInfo?

    /**
     * 根据id删除任务
     * @param id 任务id
     */
    fun deleteJobById(id: String)

    /**
     * 添加任务日志
     * @param log 日志信息
     * @return 主键id
     */
    fun addJobLog(log: JobLog): String

    /**
     * 更新任务日志
     * @param log 日志信息
     */
    fun updateJobLog(log: JobLog)

    /**
     * 分页查询任务日志
     * @param param 查询条件
     */
    fun listLogPage(param: LogQueryParam): Page<JobLog>

    /**
     * 查询运行失败的任务日志id
     * @param limit 查询数量限制
     */
    fun findFailJobLogIds(limit: Int): List<String>

    /**
     * 查询运行状态丢失的任务
     * @param triggerTime 任务触发时间
     */
    fun findLostJobLogIds(triggerTime: LocalDateTime): List<String>

    /**
     * 更新log alarm状态
     * @param logId 日志id
     * @param old 当前状态
     * @param new 新的状态
     *
     * @return 影响行数
     */
    fun updateAlarmStatus(logId: String, old: Int, new: Int): Int

    /**
     * 更新handle信息
     * @param logId 日志id
     * @param executionCode 执行状态
     * @param executionMessage 执行信息
     * @param executionTime 完成时间
     */
    fun updateExecutionResult(
        logId: String,
        executionCode: Int,
        executionMessage: String,
        executionTime: LocalDateTime,
    ): Int

    /**
     * 根据id查找任务日志
     * @param id 日志id
     */
    fun findJobLogById(id: String): JobLog?

    /**
     * 根据job id删除关联的日志
     * @param jobId 任务id
     */
    fun deleteLogByJobId(jobId: String)
}
