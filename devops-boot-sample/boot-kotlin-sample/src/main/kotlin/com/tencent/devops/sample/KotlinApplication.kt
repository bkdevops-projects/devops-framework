package com.tencent.devops.sample

import com.tencent.devops.boot.runApplication
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

/**
 * 使用DevOpsBoot框架的Sample应用
 */
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
class KotlinApplication {

    @Scheduled(fixedDelay = 60 * 1000L)
    fun schedule() {
        logger.debug("debug log")
        logger.info("info log")
        logger.error("error log")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KotlinApplication::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<KotlinApplication>(args)
}
