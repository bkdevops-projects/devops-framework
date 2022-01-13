package com.tencent.devops.schedule.router

import com.tencent.devops.schedule.pojo.trigger.TriggerParam
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.TreeMap

/**
 * 一致性hash路由
 * 不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；
 */
class WorkerRouteConsistentHash : WorkerRouter {

    private val virtualNodeCount = 100

    override fun route(triggerParam: TriggerParam, addressList: List<String>): String {
        return hashJob(triggerParam.jobId, addressList)
    }

    private fun hashJob(jobId: String, addressList: List<String>): String {
        val addressRing = TreeMap<Long, String>()
        for (address in addressList) {
            for (i in 0 until virtualNodeCount) {
                val addressHash = hash("SHARD-$address-NODE-$i")
                addressRing[addressHash] = address
            }
        }
        val jobHash = hash(jobId)
        val lastRing = addressRing.tailMap(jobHash)
        return if (!lastRing.isEmpty()) {
            lastRing[lastRing.firstKey()].orEmpty()
        } else addressRing.firstEntry().value
    }

    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值)
     * @param key
     * @return
     */
    private fun hash(key: String): Long {
        // md5 byte
        val md5: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("MD5 not supported", e)
        }
        md5.reset()
        val keyBytes = key.toByteArray(charset("UTF-8"))
        md5.update(keyBytes)
        val digest = md5.digest()

        // hash code, Truncate to 32-bits
        val hashCode = ((digest[3].toInt() and 0xFF).toLong() shl 24
                or ((digest[2].toInt() and 0xFF).toLong() shl 16)
                or ((digest[1].toInt() and 0xFF).toLong() shl 8)
                or (digest[0].toInt() and 0xFF).toLong())
        return hashCode and 0xffffffffL
    }
}
