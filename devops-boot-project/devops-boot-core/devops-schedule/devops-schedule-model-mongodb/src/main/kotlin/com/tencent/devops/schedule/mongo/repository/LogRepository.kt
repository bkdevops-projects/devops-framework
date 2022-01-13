package com.tencent.devops.schedule.mongo.repository

import com.tencent.devops.schedule.mongo.model.TJobLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LogRepository : MongoRepository<TJobLog, String> {
    fun deleteByJobId(jobId: String)
}
