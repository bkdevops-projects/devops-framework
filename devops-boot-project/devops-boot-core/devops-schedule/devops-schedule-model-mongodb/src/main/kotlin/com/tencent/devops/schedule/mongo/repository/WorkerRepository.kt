package com.tencent.devops.schedule.mongo.repository

import com.tencent.devops.schedule.mongo.model.TWorker
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WorkerRepository: MongoRepository<TWorker, String> {
    fun deleteByUpdateTimeLessThan(time: LocalDateTime): Long
    fun findAllByUpdateTimeGreaterThan(time: LocalDateTime): List<TWorker>
}
