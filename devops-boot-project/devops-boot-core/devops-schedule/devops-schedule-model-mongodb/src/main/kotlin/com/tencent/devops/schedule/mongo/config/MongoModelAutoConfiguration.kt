package com.tencent.devops.schedule.mongo.config

import com.tencent.devops.schedule.mongo.provider.MongoJobProvider
import com.tencent.devops.schedule.mongo.provider.MongoLockProvider
import com.tencent.devops.schedule.mongo.provider.MongoWorkerProvider
import com.tencent.devops.schedule.mongo.repository.JobRepository
import com.tencent.devops.schedule.mongo.repository.LogRepository
import com.tencent.devops.schedule.mongo.repository.WorkerGroupRepository
import com.tencent.devops.schedule.mongo.repository.WorkerRepository
import com.tencent.devops.schedule.provider.JobProvider
import com.tencent.devops.schedule.provider.LockProvider
import com.tencent.devops.schedule.provider.WorkerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration(proxyBeanMethods = false)
@EnableMongoRepositories(basePackages = ["com.tencent.devops.schedule.mongo.repository"])
class MongoModelAutoConfiguration(
    private val jobRepository: JobRepository,
    private val logRepository: LogRepository,
    private val workerGroupRepository: WorkerGroupRepository,
    private val workerRepository: WorkerRepository,
    private val mongoTemplate: MongoTemplate
) {

    @Bean
    fun jobProvider(): JobProvider {
        return MongoJobProvider(
            jobRepository = jobRepository,
            logRepository = logRepository,
            mongoTemplate = mongoTemplate
        )
    }

    @Bean
    fun workerProvider(): WorkerProvider {
        return MongoWorkerProvider(
            groupRepository = workerGroupRepository,
            workerRepository = workerRepository,
            mongoTemplate = mongoTemplate
        )
    }

    @Bean
    fun lockProvider(): LockProvider {
        return MongoLockProvider(mongoTemplate)
    }
}
