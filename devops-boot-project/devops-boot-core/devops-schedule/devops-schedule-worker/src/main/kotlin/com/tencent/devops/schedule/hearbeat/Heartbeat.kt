package com.tencent.devops.schedule.hearbeat

import com.tencent.devops.schedule.enums.WorkerStatusEnum

/**
 * 维持和调度中心的心跳，定时上报状态
 */
interface Heartbeat {

    /**
     * @param status 自身状态
     */
    fun beat(status: WorkerStatusEnum)

}
