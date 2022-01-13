package com.tencent.devops.schedule.constants

/**
 * server api
 */
const val SERVER_BASE_PATH = "\${devops.schedule.server.context-path:/}"
const val SERVER_API_V1 = "/api/v1"
const val SERVER_RPC_V1 = "/rpc/v1"
const val RPC_SUBMIT_RESULT = "/job/submit"
const val RPC_HEART_BEAT = "/heartbeat"

/**
 * worker api
 */
const val WORKER_RPC_V1 = "/rpc/v1"
const val RPC_RUN_JOB = "/job/run"
