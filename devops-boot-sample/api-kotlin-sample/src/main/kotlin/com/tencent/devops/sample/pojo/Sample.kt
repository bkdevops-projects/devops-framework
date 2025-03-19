package com.tencent.devops.sample.pojo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "样例")
data class Sample(
    @Schema(description = "id")
    val id: Long,
    @Schema(description = "名称")
    val name: String
)
