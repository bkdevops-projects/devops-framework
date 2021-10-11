package com.tencent.devops.schedule.hearbeat

import com.tencent.devops.schedule.constants.WORKER_BEAT_PERIOD
import com.tencent.devops.schedule.enums.WorkerStatusEnum
import com.tencent.devops.schedule.utils.sleep
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import kotlin.concurrent.thread

abstract class AbstractHeartbeat: Heartbeat, InitializingBean, DisposableBean {

    private var running = false

    private lateinit var thread: Thread

    override fun afterPropertiesSet() {
        running = true
        thread = thread(
            isDaemon = true,
            name = "heart-beat-thread"
        ) {
            while (running) {
                beat(WorkerStatusEnum.RUNNING)
                sleep(WORKER_BEAT_PERIOD)
            }
            beat(WorkerStatusEnum.STOP)
        }
    }

    override fun destroy() {
        running = false
    }
}
