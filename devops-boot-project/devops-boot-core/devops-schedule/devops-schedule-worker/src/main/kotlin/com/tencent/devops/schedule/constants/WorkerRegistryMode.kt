package com.tencent.devops.schedule.constants

/**
 * worker注册模式
 */
enum class WorkerRegistryMode {
    /**
     * 自动注册，worker启动后自动注册到调度中心
     */
    AUTO,
    /**
     * 手动注册，由调度中心手动注册worker
     */
    MANUAL,
    /**
     * 自动发现，依赖spring-cloud的服务发现
     */
    DISCOVERY;
}
