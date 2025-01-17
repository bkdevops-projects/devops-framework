package com.tencent.devops.schedule.mongo.provider

import com.tencent.devops.schedule.enums.AlarmStatusEnum
import com.tencent.devops.schedule.enums.ExecutionCodeEnum
import com.tencent.devops.schedule.enums.TriggerCodeEnum
import com.tencent.devops.schedule.enums.TriggerStatusEnum
import com.tencent.devops.schedule.mongo.model.TJobInfo
import com.tencent.devops.schedule.mongo.model.TJobLog
import com.tencent.devops.schedule.mongo.model.convert
import com.tencent.devops.schedule.mongo.repository.JobRepository
import com.tencent.devops.schedule.mongo.repository.LogRepository
import com.tencent.devops.schedule.pojo.job.JobInfo
import com.tencent.devops.schedule.pojo.job.JobQueryParam
import com.tencent.devops.schedule.pojo.log.JobLog
import com.tencent.devops.schedule.pojo.log.LogQueryParam
import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.provider.JobProvider
import com.tencent.devops.schedule.utils.ofPageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

/**
 * 基于mongodb实现的job
 */
class MongoJobProvider(
    private val jobRepository: JobRepository,
    private val logRepository: LogRepository,
    private val mongoTemplate: MongoTemplate,
) : JobProvider {
    override fun addJob(jobInfo: JobInfo): String {
        return jobInfo.convert().apply { jobRepository.save(this) }.id.orEmpty()
    }

    override fun updateJob(job: JobInfo) {
        jobRepository.save(job.convert())
    }

    override fun updateJobSchedule(job: JobInfo) {
        val query = Query.query(where(TJobInfo::id).`is`(job.id))
        val update = Update().set(TJobInfo::lastTriggerTime.name, job.lastTriggerTime)
            .set(TJobInfo::nextTriggerTime.name, job.nextTriggerTime)
            .set(TJobInfo::triggerStatus.name, job.triggerStatus)
        mongoTemplate.updateFirst(query, update, TJobInfo::class.java)
    }

    override fun listJobPage(param: JobQueryParam): Page<JobInfo> {
        val criteria = Criteria()
        with(param) {
            name?.let { criteria.and(TJobInfo::name).regex("^$it") }
            groupId?.let { criteria.and(TJobInfo::groupId).isEqualTo(it) }
            triggerStatus?.let { criteria.and(TJobInfo::triggerStatus).isEqualTo(it) }
        }
        val query = Query.query(criteria)
        val total = mongoTemplate.count(query, TJobInfo::class.java)
        val pageable = param.ofPageable()
        val records = mongoTemplate.find(query.with(pageable), TJobInfo::class.java).map { it.convert() }
        return Page(
            pageNumber = param.pageNumber,
            pageSize = param.pageSize,
            totalRecords = total,
            records = records,
        )
    }

    override fun findTodoJobs(time: Long, limit: Int): List<JobInfo> {
        val criteria = where(TJobInfo::nextTriggerTime).lte(time)
            .and(TJobInfo::triggerStatus).isEqualTo(TriggerStatusEnum.RUNNING.code())
        val query = Query(criteria).limit(limit)
        return mongoTemplate.find(query, TJobInfo::class.java).map { it.convert() }
    }

    override fun findJobById(id: String): JobInfo? {
        return jobRepository.findByIdOrNull(id)?.convert()
    }

    override fun deleteJobById(id: String) {
        jobRepository.deleteById(id)
    }

    override fun addJobLog(log: JobLog): String {
        return log.convert().apply { logRepository.save(this) }.id.orEmpty()
    }

    override fun findJobLogById(id: String): JobLog? {
        return logRepository.findByIdOrNull(id)?.convert()
    }

    override fun updateJobLog(log: JobLog) {
        logRepository.save(log.convert())
    }

    override fun listLogPage(param: LogQueryParam): Page<JobLog> {
        val criteria = Criteria()
        with(param) {
            jobId?.let { criteria.and(TJobLog::jobId).isEqualTo(it) }
            triggerTimeFrom?.let { from ->
                criteria.and(TJobLog::triggerTime).gte(from).apply {
                    triggerTimeTo?.let { to -> lte(to) }
                }
            } ?: run {
                triggerTimeTo?.let { to -> criteria.and(TJobLog::triggerTime).lte(to) }
            }
            executionCode?.let { criteria.and(TJobLog::executionCode).isEqualTo(it) }
            triggerCode?.let { criteria.and(TJobLog::triggerCode).isEqualTo(it) }
        }
        val query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, TJobLog::triggerTime.name))
        val total = mongoTemplate.count(query, TJobLog::class.java)
        val pageable = param.ofPageable()
        val records = mongoTemplate.find(query.with(pageable), TJobLog::class.java).map { it.convert() }
        return Page(
            pageNumber = param.pageNumber,
            pageSize = param.pageSize,
            totalRecords = total,
            records = records,
        )
    }

    override fun findFailJobLogIds(limit: Int): List<String> {
        val pageable = PageRequest.of(0, limit)
        val criteria = where(TJobLog::alarmStatus).isEqualTo(AlarmStatusEnum.TODO.code())
            .orOperator(
                where(TJobLog::triggerCode).lt(TriggerCodeEnum.INITIALED.code()),
                where(TJobLog::executionCode).lt(ExecutionCodeEnum.INITIALED.code()),
            )
        val query = Query.query(criteria).with(pageable)
        query.fields().include(TJobLog::id.name)
        return mongoTemplate.find(query, IdEntity::class.java, "job_log").map { it.id }
    }

    override fun findLostJobLogIds(triggerTime: LocalDateTime): List<String> {
        val criteria = where(TJobLog::triggerCode).isEqualTo(TriggerCodeEnum.SUCCESS.code())
            .and(TJobLog::executionCode).isEqualTo(ExecutionCodeEnum.INITIALED.code())
            .and(TJobLog::triggerTime).lt(triggerTime)
        val query = Query.query(criteria)
        query.fields().include(TJobLog::id.name)
        return mongoTemplate.find(query, IdEntity::class.java, "job_log").map { it.id }
    }

    override fun deleteLogByJobId(jobId: String) {
        logRepository.deleteByJobId(jobId)
    }

    override fun updateAlarmStatus(logId: String, old: Int, new: Int): Int {
        val criteria = where(TJobLog::id).`is`(logId).and(TJobLog::alarmStatus).`is`(old)
        val query = Query.query(criteria)
        val update = Update().set(TJobLog::alarmStatus.name, new)
        return mongoTemplate.updateFirst(query, update, TJobLog::class.java).modifiedCount.toInt()
    }

    override fun updateExecutionResult(
        logId: String,
        executionCode: Int,
        executionMessage: String,
        executionTime: LocalDateTime,
    ): Int {
        val criteria = where(TJobLog::id).`is`(logId)
        val query = Query.query(criteria)
        val update = Update().set(TJobLog::executionCode.name, executionCode)
            .set(TJobLog::executionMsg.name, executionMessage)
            .set(TJobLog::executionTime.name, executionTime)
        return mongoTemplate.updateFirst(query, update, TJobLog::class.java).modifiedCount.toInt()
    }

    override fun countByWorkerAddress(executionCode: Int, workerAddress: String): Int {
        val criteria = where(TJobLog::executionCode).`is`(executionCode).and(TJobLog::workerAddress).`is`(workerAddress)
        val query = Query.query(criteria)
        return mongoTemplate.count(query,"job_log").toInt()
    }

    data class IdEntity(val id: String)
}
