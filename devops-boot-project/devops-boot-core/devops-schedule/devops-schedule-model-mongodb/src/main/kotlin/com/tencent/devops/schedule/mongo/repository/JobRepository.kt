package com.tencent.devops.schedule.mongo.repository

import com.tencent.devops.schedule.mongo.model.TJobInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: MongoRepository<TJobInfo, String>
