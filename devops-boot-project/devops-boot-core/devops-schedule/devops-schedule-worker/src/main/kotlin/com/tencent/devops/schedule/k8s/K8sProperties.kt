package com.tencent.devops.schedule.k8s

data class K8sProperties(
    var namespace: String = "default",
    var apiServer: String? = null,
    var token: String? = null,
    var limit: ResourceLimitProperties = ResourceLimitProperties(),
)
