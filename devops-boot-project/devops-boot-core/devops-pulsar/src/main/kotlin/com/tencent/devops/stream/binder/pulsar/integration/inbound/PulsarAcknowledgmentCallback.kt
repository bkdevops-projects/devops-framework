/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.stream.binder.pulsar.integration.inbound

import org.apache.pulsar.client.api.Consumer
import org.apache.pulsar.client.api.Message
import org.slf4j.LoggerFactory
import org.springframework.integration.acks.AcknowledgmentCallback
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Pulsar 消息确认回调实现
 * 用于支持业务层手动控制消息确认
 */
class PulsarAcknowledgmentCallback(
    private val consumer: Consumer<*>,
    private val message: Message<*>
) : AcknowledgmentCallback {

    private val acknowledged = AtomicBoolean(false)
    private val autoAckEnabled = AtomicBoolean(false)

    companion object {
        private val log = LoggerFactory.getLogger(PulsarAcknowledgmentCallback::class.java)
    }

    /**
     * 确认消息
     * @return true 如果确认成功，false 如果已经确认过
     */
    override fun acknowledge(status: AcknowledgmentCallback.Status) {
        if (acknowledged.compareAndSet(false, true)) {
            try {
                when (status) {
                    AcknowledgmentCallback.Status.ACCEPT -> {
                        // 正常确认消息
                        consumer.acknowledge(message)
                        if (log.isDebugEnabled) {
                            log.debug("Message acknowledged: ${message.messageId}")
                        }
                    }

                    AcknowledgmentCallback.Status.REJECT -> {
                        // 拒绝消息，不重试
                        consumer.negativeAcknowledge(message)
                        if (log.isDebugEnabled) {
                            log.debug("Message rejected: ${message.messageId}")
                        }
                    }

                    AcknowledgmentCallback.Status.REQUEUE -> {
                        // 重新入队，触发重试
                        consumer.reconsumeLater(message, 1000, TimeUnit.MILLISECONDS) // 延迟1秒重新消费
                        if (log.isDebugEnabled) {
                            log.debug("Message requeued: ${message.messageId}")
                        }
                    }
                }
            } catch (e: Exception) {
                log.error("Error while acknowledging message ${message.messageId}", e)
            }
        } else {
            log.warn("Message ${message.messageId} has already been acknowledged")
        }
    }

    /**
     * 判断消息是否已确认
     */
    override fun isAcknowledged(): Boolean {
        return acknowledged.get()
    }

    /**
     * 不支持自动确认
     */
    override fun noAutoAck() {
        autoAckEnabled.set(false)
    }

    /**
     * 判断是否自动确认
     */
    override fun isAutoAck(): Boolean {
        return autoAckEnabled.get()
    }
}

