package com.tencent.devops.schedule.hearbeat

import com.tencent.devops.schedule.api.ServerRpcClient
import com.tencent.devops.schedule.config.ScheduleWorkerProperties
import com.tencent.devops.schedule.enums.WorkerStatusEnum
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException

class DefaultHeartbeat(
    private val workerProperties: ScheduleWorkerProperties,
    private val serverRpcClient: ServerRpcClient
): AbstractHeartbeat() {

    @Value("\${spring.application.name: application}")
    private var application: String = ""

    @Value("\${server.port: 8080}")
    private var port: String = ""

    private var group: String = workerProperties.group
    private var address: String = workerProperties.address

    override fun afterPropertiesSet() {
        if (group.isBlank()) {
            // 取服务名称
            group = application
        }
        if (address.isBlank()) {
            // 自动获取ip
            val builder = StringBuilder()
                .append("http://")
                .append(findFirstNonLoopBackAddress()?.hostAddress)
                .append(":")
                .append(port)
            address = builder.toString()

        }
        super.afterPropertiesSet()
    }

    override fun beat(status: WorkerStatusEnum) {
        try {
            val param = HeartBeatParam(
                group = group,
                address = address,
                status = status.code()
            )
            serverRpcClient.heartBeat(param)
            logger.debug("update worker status[$status] success.")
        } catch (e: Exception) {
            logger.error("update worker status[$status] error: ${e.message}", e)
        }
    }

    private fun findFirstNonLoopBackAddress(): InetAddress? {
        var result: InetAddress? = null
        try {
            var lowest = Int.MAX_VALUE
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val ifc = interfaces.nextElement()
                if (ifc.isUp) {
                    logger.trace("Testing interface: " + ifc.displayName)
                    if (ifc.index < lowest || result == null) {
                        lowest = ifc.index
                    } else if (result != null) {
                        continue
                    }

                    // @formatter:off
                    val addresses = ifc.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val address = addresses.nextElement()
                        if (address is Inet4Address && !address.isLoopbackAddress()) {
                            logger.trace("Found non-loopback interface: " + ifc.displayName)
                            result = address
                        }
                    }
                    // @formatter:on
                }
            }
        } catch (ex: IOException) {
            logger.error("Cannot get first non-loopback address", ex)
        }

        if (result != null) {
            return result
        }

        try {
            return InetAddress.getLocalHost()
        } catch (e: UnknownHostException) {
            logger.warn("Unable to retrieve localhost")
        }

        return null
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultHeartbeat::class.java)
    }
}
