package com.tencent.devops.schedule.k8s

/**
 * 资源限制
 * */
data class ResourceLimitProperties(
    val limitMem: Long = 32 * GB,
    val limitStorage: Long = 128 * GB,
    val limitCpu: Double = 16.0,
    val requestMem: Long = 16 * GB,
    val requestStorage: Long = 16 * GB,
    val requestCpu: Double = 4.0,
) {
    companion object {
        private const val GB = 1024 * 1024 * 1024L
    }
}
