package com.tencent.devops.schedule.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CloudWorkerApplication

fun main(args: Array<String>) {
    runApplication<CloudWorkerApplication>(*args)
}
