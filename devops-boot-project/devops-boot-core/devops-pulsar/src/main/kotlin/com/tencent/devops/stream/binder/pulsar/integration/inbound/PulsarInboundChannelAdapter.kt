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

import com.tencent.devops.stream.binder.pulsar.properties.AckMode
import com.tencent.devops.stream.binder.pulsar.properties.PulsarConsumerProperties
import com.tencent.devops.stream.binder.pulsar.properties.PulsarProperties
import com.tencent.devops.stream.binder.pulsar.support.PulsarMessageConverterSupport
import com.tencent.devops.stream.binder.pulsar.util.PulsarClientUtils
import com.tencent.devops.stream.binder.pulsar.util.PulsarUtils
import org.apache.pulsar.client.api.Consumer
import org.apache.pulsar.client.api.Message
import org.apache.pulsar.client.api.PulsarClient
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties
import org.springframework.integration.context.OrderlyShutdownCapable
import org.springframework.integration.endpoint.MessageProducerSupport
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessagingException
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.support.RetryTemplate
import org.springframework.util.Assert

class PulsarInboundChannelAdapter(
    private val destination: String,
    private val group: String? = null,
    private var extendedConsumerProperties: ExtendedConsumerProperties<PulsarConsumerProperties>,
    private val pulsarProperties: PulsarProperties
) : MessageProducerSupport(), OrderlyShutdownCapable {
    private val consumers: MutableList<Consumer<Any>> = mutableListOf()
    var retryTemplate: RetryTemplate? = null
    var recoveryCallback: RecoveryCallback<Any>? = null
    var deadLetter: String? = null
    var retryLetter: String? = null
    var pulsarClient: PulsarClient? = null
    private var topic: String = ""
    override fun onInit() {
        if (extendedConsumerProperties.extension == null) {
            return
        }
        try {
            super.onInit()
            topic = PulsarUtils.generateTopic(
                tenant = pulsarProperties.tenant,
                namespace = pulsarProperties.namespace,
                topic = destination
            )
            val (dLetter, rLetter) = PulsarUtils.generateDeadLetterTopics(
                tenant = pulsarProperties.tenant,
                namespace = pulsarProperties.namespace,
                group = group,
                subscriptionName = extendedConsumerProperties.extension.subscriptionName,
                deadLetterTopic = extendedConsumerProperties.extension.deadLetterTopic,
                retryLetterTopic = extendedConsumerProperties.extension.retryLetterTopic
            )
            deadLetter = dLetter
            retryLetter = rLetter
            createRetryTemplate()
            pulsarClient = PulsarClientUtils.pulsarClient(
                pulsarProperties = pulsarProperties,
                concurrency = extendedConsumerProperties.concurrency
            )
        } catch (e: Exception) {
            log.error("DefaultPulsarConsumer init failed, Caused by " + e.message)
            throw MessagingException(
                MessageBuilder.withPayload(
                    "DefaultPulsarConsumer init failed, Caused by " + e.message
                ).build(),
                e
            )
        }
    }

    private fun createRetryTemplate() {
        retryTemplate?.let {
            Assert.state(
                errorChannel == null,
                "Cannot have an 'errorChannel' property when a 'RetryTemplate' is " +
                    "provided; use an 'ErrorMessageSendingRecoverer' in the 'recoveryCallback' property to " +
                    "send an error message when retries are exhausted"
            )
            retryTemplate!!.registerListener(object : RetryListener {
                override fun <T, E : Throwable?> open(
                    context: RetryContext,
                    callback: RetryCallback<T, E>
                ): Boolean {
                    return true
                }

                override fun <T, E : Throwable?> close(
                    context: RetryContext,
                    callback: RetryCallback<T, E>,
                    throwable: Throwable?
                ) = Unit

                override fun <T, E : Throwable?> onError(
                    context: RetryContext,
                    callback: RetryCallback<T, E>,
                    throwable: Throwable?
                ) = Unit
            })
        }
    }

    private fun createListener(): (Consumer<*>, Message<*>) -> Unit {
        return { consumer: Consumer<*>, msg: Message<*> ->
            try {
                if (log.isDebugEnabled) {
                    log.debug("Message received ${msg.messageId}")
                }

                // 根据 ACK 模式决定是否创建 ACK 回调
                val ackCallback = if (extendedConsumerProperties.extension.ackMode == AckMode.MANUAL.name) {
                    PulsarAcknowledgmentCallback(consumer, msg)
                } else {
                    null
                }

                val message = PulsarMessageConverterSupport.convertMessage2Spring(msg, ackCallback)

                if (retryTemplate != null) {
                    retryTemplate!!.execute(
                        RetryCallback<Any, RuntimeException> { _: RetryContext? ->
                            sendMessage(message)

                            // 自动 ACK 模式下，消息处理完成后自动确认
                            if (extendedConsumerProperties.extension.ackMode == AckMode.AUTO.name) {
                                if (log.isDebugEnabled) {
                                    log.debug("will send acknowledge: ${msg.messageId}")
                                }
                                consumer.acknowledge(msg)
                            } else {
                                // 手动 ACK 模式下，记录日志提示业务层需要手动确认
                                if (log.isDebugEnabled) {
                                    log.debug("Manual ACK mode, waiting for business layer to acknowledge: ${msg.messageId}")
                                }
                            }
                            message
                        },
                        recoveryCallback
                    )
                } else {
                    sendMessage(message)

                    // 自动 ACK 模式下，消息处理完成后自动确认
                    if (extendedConsumerProperties.extension.ackMode == AckMode.AUTO.name) {
                        if (log.isDebugEnabled) {
                            log.debug("will send acknowledge: ${msg.messageId}")
                        }
                        consumer.acknowledge(msg)
                    } else {
                        // 手动 ACK 模式下，记录日志提示业务层需要手动确认
                        if (log.isDebugEnabled) {
                            log.debug("Manual ACK mode, waiting for business layer to acknowledge: ${msg.messageId}")
                        }
                    }
                }

                if (log.isDebugEnabled) {
                    log.debug("Message ${msg.messageId} has been processed")
                }
            } catch (e: Exception) {
                log.warn("Error occurred while consuming message ${msg.messageId}: $e")

                // 如果是自动 ACK 模式或者发生异常，发送 negative ack
                if (extendedConsumerProperties.extension.ackMode == AckMode.AUTO.name) {
                    consumer.negativeAcknowledge(msg)
                } else {
                    // 手动 ACK 模式下，即使发生异常也不自动 negative ack
                    // 让业务层通过 AcknowledgmentCallback 处理
                    log.warn("Manual ACK mode, exception occurred but not sending negative acknowledgement automatically")
                }
            }
        }
    }

    override fun doStart() {
        if (extendedConsumerProperties.extension == null) {
            return
        }
        val messageListener = createListener()
        // TODO multi topic如何处理， batch 如何处理， 对于Subscription多种模式如何处理
        try {
            for (i in 1..extendedConsumerProperties.concurrency) {
                val consumer = PulsarConsumerFactory.initPulsarConsumer(
                    topic = topic,
                    group = group,
                    consumerProperties = extendedConsumerProperties,
                    messageListener = messageListener,
                    deadLetterTopic = deadLetter!!,
                    retryLetterTopic = retryLetter!!,
                    pulsarProperties = pulsarProperties,
                    concurrency = extendedConsumerProperties.concurrency,
                    pulsarClient = pulsarClient
                )
                consumers.add(consumer)
            }
        } catch (e: Exception) {
            log.error("Error occurred while creating consumer $e")
        }

//        val instrumentation = Instrumentation(topic, this)
//        try {
//            instrumentation.markStartedSuccessfully()
//        } catch (e: java.lang.Exception) {
//            instrumentation.markStartFailed(e)
//            log.error("PulsarConsumer init failed, Caused by " + e.message)
//            throw MessagingException(
//                MessageBuilder.withPayload(
//                    "PulsarConsumer init failed, Caused by " + e.message
//                )
//                    .build(),
//                e
//            )
//        } finally {
//            InstrumentationManager.addHealthInstrumentation(instrumentation)
//        }
    }

    override fun doStop() {
        // 2.8.1版本调用会出错，所以升级https://github.com/apache/pulsar/issues/12024
        consumers.forEach {
            try {
                it.close()
            } catch (ignore: Exception) {
            }
        }
    }

    override fun beforeShutdown(): Int {
        this.stop()
        return 0
    }

    override fun afterShutdown(): Int {
        return 0
    }

    companion object {
        // 命名为logger会和IntegrationObjectSupport中的logger冲突
        // https://youtrack.jetbrains.com/issue/KT-56386
        private val log = LoggerFactory.getLogger(PulsarInboundChannelAdapter::class.java)
    }
}
