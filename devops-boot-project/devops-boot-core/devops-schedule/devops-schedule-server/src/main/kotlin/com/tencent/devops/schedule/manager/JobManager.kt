package com.tencent.devops.schedule.manager

import com.tencent.devops.schedule.pojo.job.JobCreateRequest
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.job.JobQueryParam
import com.tencent.devops.schedule.pojo.job.JobUpdateRequest
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.log.LogQueryParam
import com.tencent.devops.schedule.pojo.page.Page
import java.time.LocalDateTime

/**
 * 任务管理器接口
 */
interface JobManager {

    /**
     * 创建任务
     * @param request 创建任务请求
     * @return 任务id
     */
    fun createJob(request: JobCreateRequest): String

    /**
     * 更新任务
     * @param request 更新任务请求
     */
    fun updateJob(request: JobUpdateRequest)

    /**
     * 启动任务
     * @param id 任务id
     */
    fun startJob(id: String)

    /**
     * 停止任务
     * @param id 任务id
     */
    fun stopJob(id: String)

    /**
     * 删除任务
     * @param id 任务id
     */
    fun deleteJob(id: String)

    /**
     * 触发任务
     * @param id 任务id
     * @param executorParam 任务参数
     * */
    fun triggerJob(id: String, executorParam: String?)

    /**
     * 更新任务调度信息
     * 只更新调度状态、上次调度时间、下次调度时间
     * @param job 任务信息
     */
    fun updateJobSchedule(job: JobInfo)

    /**
     * 根据id查询任务信息
     * @param id 任务id
     */
    fun findJobById(id: String): JobInfo?

    /**
     * 分页查询任务
     */
    fun listJobPage(param: JobQueryParam): Page<JobInfo>

    /**
     * 加载在指定时间前触发的任务列表
     * @param time 指定时间
     */
    fun findTodoJobs(time: Long): List<JobInfo>

    /**
     * 添加任务日志
     * @param jobLog 任务日志
     * @return 主键id
     */
    fun addJobLog(jobLog: JobLog): String

    /**
     * 更新任务日志
     * @param jobLog 任务日志
     */
    fun updateJobLog(jobLog: JobLog)

    /**
     * 分页查询日志
     * @param param 查询参数
     */
    fun listLogPage(param: LogQueryParam): Page<JobLog>

    /**
     * 查询运行失败的任务
     * 运行失败指触发失败或执行失败
     * @param limit 查询数量限制
     */
    fun findFailJobLogIds(limit: Int): List<String>

    /**
     * 查询运行状态丢失的任务
     * 状态丢失指触发成功，但超过指定时间没有执行结果
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
     * 根据id查找任务日志
     * @param id 日志id
     */
    fun findJobLogById(id: String): JobLog?

    /**
     * 结束任务
     * @param logId 日志id
     * @param executionCode 执行结果码
     * @param executionMessage 执行结果
     */
    fun completeJob(logId: String, executionCode: Int, executionMessage: String?)
}
