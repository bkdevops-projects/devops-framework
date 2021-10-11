package com.tencent.devops.schedule.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val logger: Logger = LoggerFactory.getLogger("ThreadUtils")

fun alignTime(period: Long) {
    if (period < 1000) {
        return
    }
    try {
        TimeUnit.MILLISECONDS.sleep(period - System.currentTimeMillis() % 1000)
    } catch (e: InterruptedException) {
        logger.warn("align time is interrupted: ${e.message}")
    }
}

fun sleep(seconds: Int) {
    try {
        TimeUnit.SECONDS.sleep(seconds.toLong())
    } catch (e: InterruptedException) {
        logger.warn("sleep is interrupted: ${e.message}")
    }
}

fun terminate(thread: Thread) {
    if (thread.state != Thread.State.TERMINATED) {
        thread.interrupt()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            logger.error("join is interrupted: ${e.message}", e)
        }
    }
}
