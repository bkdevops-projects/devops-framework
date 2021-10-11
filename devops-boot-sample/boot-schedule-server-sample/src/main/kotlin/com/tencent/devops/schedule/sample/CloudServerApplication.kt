package com.tencent.devops.schedule.sample

import com.tencent.devops.schedule.config.EnableScheduleServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableScheduleServer
@SpringBootApplication
class CloudServerApplication

fun main(args: Array<String>) {
    runApplication<CloudServerApplication>(*args)
}
