package com.tencent.devops.schedule.mongo.provider

import com.tencent.devops.schedule.mongo.model.TWorker
import com.tencent.devops.schedule.mongo.model.TWorkerGroup
import com.tencent.devops.schedule.mongo.model.convert
import com.tencent.devops.schedule.mongo.repository.WorkerGroupRepository
import com.tencent.devops.schedule.mongo.repository.WorkerRepository
import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.pojo.worker.WorkerGroupName
import com.tencent.devops.schedule.pojo.worker.WorkerGroupQueryParam
import com.tencent.devops.schedule.pojo.worker.WorkerInfo
import com.tencent.devops.schedule.provider.WorkerProvider
import com.tencent.devops.schedule.utils.ofPageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.where
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

/**
 * 基于mongodb实现的worker provider
 */
class MongoWorkerProvider(
    private val groupRepository: WorkerGroupRepository,
    private val workerRepository: WorkerRepository,
    private val mongoTemplate: MongoTemplate
) : WorkerProvider {
    override fun findGroupByName(name: String): WorkerGroup? {
        return groupRepository.findByName(name)?.convert()
    }

    override fun findGroupById(id: String): WorkerGroup? {
        return groupRepository.findByIdOrNull(id)?.convert()
    }

    override fun addGroup(workerGroup: WorkerGroup): String {
        return workerGroup.convert().apply { groupRepository.save(this) }.id.orEmpty()
    }

    override fun updateGroup(group: WorkerGroup) {
        group.convert().apply { groupRepository.save(this) }
    }

    override fun upsertWorker(group: String, address: String) {
        val criteria = where(TWorker::group).`is`(group).and(TWorker::address).`is`(address)
        val query = Query(criteria)
        val update = Update.update(TWorker::updateTime.name, LocalDateTime.now())
        mongoTemplate.upsert(query, update, TWorker::class.java)
    }

    override fun deleteWorker(group: String, address: String) {
        val criteria = where(TWorker::group).`is`(group).and(TWorker::address).`is`(address)
        val query = Query(criteria)
        mongoTemplate.remove(query, TWorker::class.java)
    }

    override fun deleteWorkerGroup(id: String) {
        groupRepository.deleteById(id)
    }

    override fun listGroupPage(param: WorkerGroupQueryParam): Page<WorkerGroup> {
        val criteria = Criteria()
        with(param) {
            name?.let { criteria.and(TWorkerGroup::name).regex("^$it") }
        }
        val query = Query.query(criteria)
        val total = mongoTemplate.count(query, TWorkerGroup::class.java)
        val pageable = param.ofPageable()
        val records = mongoTemplate.find(query.with(pageable), TWorkerGroup::class.java).map { it.convert()  }
        return Page(
            pageNumber = param.pageNumber,
            pageSize = param.pageSize,
            totalRecords = total,
            records = records
        )
    }

    override fun listGroupName(): List<WorkerGroupName> {
        return groupRepository.findAll().map {
            WorkerGroupName(
                id = it.id.orEmpty(),
                name = it.name
            )
        }
    }

    override fun listGroupByDiscoveryType(type: Int): List<WorkerGroup> {
        return groupRepository.findAllGroupByDiscoveryType(type).map { it.convert() }
    }

    override fun deleteWorkerByUpdateTimeLessThan(time: LocalDateTime): Long {
        return workerRepository.deleteByUpdateTimeLessThan(time)
    }

    override fun listWorkerByUpdateTimeGreaterThan(time: LocalDateTime): List<WorkerInfo> {
        return workerRepository.findAllByUpdateTimeGreaterThan(time).map { it.convert() }
    }
}
