package com.tencent.devops.sample.pojo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("样例")
data class Sample(
    @ApiModelProperty("id")
    val id: Long,
    @ApiModelProperty("名称")
    val name: String
)
