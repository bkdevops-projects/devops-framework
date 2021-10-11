package com.tencent.devops.schedule.constants

import java.time.format.DateTimeFormatter

/**
 * 时间格式
 */
const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

/**
 * 时间格式类
 */
val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

/**
 * 任务日志message最大长度
 */
const val MAX_LOG_MESSAGE_SIZE = 8192

/**
 * 加载job时锁名称
 */
const val JOB_LOAD_LOCK_KEY = "JOB-LOAD-KEY"

/**
 * 预加载时间，ms
 */
const val PRE_LOAD_TIME = 5 * 1000L

/**
 * worker状态刷新间隔
 */
const val WORKER_BEAT_PERIOD = 30

/**
 * auth header
 */
const val SCHEDULE_RPC_AUTH_HEADER = "X-SCHEDULE-RPC-TOKEN"
const val SCHEDULE_API_AUTH_HEADER = "X-SCHEDULE-API-TOKEN"
