package com.tencent.devops.schedule.pojo.job

import com.tencent.devops.schedule.pojo.page.BasePageRequest

class JobQueryParam(
   var name: String? = null,
   var groupId: String? = null,
   var triggerStatus: Int? = null
): BasePageRequest()
