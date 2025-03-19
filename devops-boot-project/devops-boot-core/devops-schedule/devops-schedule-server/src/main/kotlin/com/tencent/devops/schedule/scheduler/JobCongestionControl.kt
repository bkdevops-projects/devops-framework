package com.tencent.devops.schedule.scheduler

import org.slf4j.LoggerFactory

/**
 * 任务拥塞控制
 * 采用TCP Reno算法，开始时进入慢速启动阶段，窗口数指数上升，
 * 当延迟超过阈值时，将窗口数减少至一半，然后进入拥塞避免阶段，窗口数线性增加
 * */
class JobCongestionControl(private val maxLatency: Long) {
    private var cwnd: Int = 1 // 拥塞窗口大小
    private var ssthresh: Int = 256 // 拥塞阈值
    private var state: State = State.SLOW_START

    fun updateLatency(latency: Long) {
        if (latency < maxLatency) {
            onGood()
        } else {
            onBad()
        }
    }

    private fun onGood() {
        when (state) {
            State.SLOW_START -> {
                cwnd *= 2 // 每次确认时窗口大小翻倍
                if (cwnd >= ssthresh) {
                    state = State.CONGESTION_AVOIDANCE
                }
            }

            State.CONGESTION_AVOIDANCE -> {
                cwnd = minOf(MAX_CWND, cwnd + 1) // 每次确认时窗口大小线性增加
            }
        }
    }

    private fun onBad() {
        ssthresh = (cwnd / 2).coerceAtLeast(1) // 更新阈值
        cwnd = ssthresh // 重置窗口大小
        state = State.CONGESTION_AVOIDANCE // 进入拥塞避免
    }

    fun getCwnd(): Int {
        return cwnd
    }

    enum class State {
        SLOW_START,
        CONGESTION_AVOIDANCE,
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobCongestionControl::class.java)
        private const val MAX_CWND = 1000000
    }
}
