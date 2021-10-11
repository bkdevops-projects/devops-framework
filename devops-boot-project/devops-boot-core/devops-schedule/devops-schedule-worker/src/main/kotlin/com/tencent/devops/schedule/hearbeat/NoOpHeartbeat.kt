package com.tencent.devops.schedule.hearbeat

import com.tencent.devops.schedule.enums.WorkerStatusEnum

class NoOpHeartbeat: Heartbeat {
    override fun beat(status: WorkerStatusEnum) {
        // do nothing
    }
}
