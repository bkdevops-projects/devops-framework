package com.tencent.devops.schedule.pojo.log

import com.tencent.devops.schedule.pojo.page.BasePageRequest
import java.time.LocalDateTime

class LogQueryParam(
    var jobId: String? = null,
    var triggerTime: List<String>? = null,
    var executionCode: Int? = null,
    var triggerCode: Int? = null,
    var triggerTimeFrom: LocalDateTime? = null,
    var triggerTimeTo: LocalDateTime? = null,
) : BasePageRequest()
