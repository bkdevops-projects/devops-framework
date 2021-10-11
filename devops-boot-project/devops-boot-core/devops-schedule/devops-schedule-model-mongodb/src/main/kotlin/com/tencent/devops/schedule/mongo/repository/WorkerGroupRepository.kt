package com.tencent.devops.schedule.mongo.repository

import com.tencent.devops.schedule.mongo.model.TWorkerGroup
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkerGroupRepository: MongoRepository<TWorkerGroup, String> {
    fun findByName(name: String): TWorkerGroup?
    fun findAllGroupByDiscoveryType(type: Int): List<TWorkerGroup>
}
