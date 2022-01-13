package com.tencent.devops.schedule.utils

import com.tencent.devops.schedule.enums.ScheduleTypeEnum
import com.tencent.devops.schedule.pojo.job.JobInfo
import org.springframework.scheduling.support.CronExpression
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 计算下次执行时间
 */
fun computeNextTriggerTime(
    job: JobInfo,
    from: LocalDateTime = LocalDateTime.now()
): Long? {
    val scheduleType = ScheduleTypeEnum.ofCode(job.scheduleType)
    val scheduleConf = job.scheduleConf
    val nextTriggerTime = when (scheduleType) {
        ScheduleTypeEnum.IMMEDIATELY -> LocalDateTime.now()
        ScheduleTypeEnum.FIX_TIME -> LocalDateTime.parse(scheduleConf)
        ScheduleTypeEnum.FIX_RATE -> from.plusSeconds(scheduleConf.toLong())
        ScheduleTypeEnum.CRON -> CronExpression.parse(scheduleConf).next(from)
        else -> return null
    }
    return nextTriggerTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
}
