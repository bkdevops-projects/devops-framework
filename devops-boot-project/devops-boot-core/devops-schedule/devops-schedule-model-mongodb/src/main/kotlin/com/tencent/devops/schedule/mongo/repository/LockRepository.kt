package com.tencent.devops.schedule.mongo.repository

import com.tencent.devops.schedule.mongo.model.TLockInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LockRepository: MongoRepository<TLockInfo, String>
