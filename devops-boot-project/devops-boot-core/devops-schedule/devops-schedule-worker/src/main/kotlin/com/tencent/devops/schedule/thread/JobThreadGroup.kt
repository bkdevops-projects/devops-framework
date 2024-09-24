package com.tencent.devops.schedule.thread

import com.tencent.devops.schedule.api.ServerRpcClient
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

/**
 * 任务线程组
 * */
class JobThreadGroup(nThreads: Int, serverRpcClient: ServerRpcClient) : AutoCloseable {
    private val threads: MutableList<JobThread> = mutableListOf()
    private val idx = AtomicLong()

    init {
        for (i in 0 until nThreads) {
            val jobThread = JobThread(serverRpcClient)
            jobThread.start()
            threads.add(jobThread)
        }
    }

    fun next(): JobThread {
        return threads[abs((idx.getAndIncrement() % threads.size).toDouble()).toInt()]
    }

    override fun close() {
        threads.forEach {
            it.toStop()
            it.join()
        }
    }
}
