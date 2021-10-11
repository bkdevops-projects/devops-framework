package com.tencent.devops.schedule.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CloudServerApplication

fun main(args: Array<String>) {
    runApplication<CloudServerApplication>(*args)
}
