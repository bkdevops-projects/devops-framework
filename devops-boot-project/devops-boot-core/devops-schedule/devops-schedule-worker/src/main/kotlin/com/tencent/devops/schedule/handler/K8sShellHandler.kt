package com.tencent.devops.schedule.handler

import com.tencent.devops.common.time.toEpochMilli
import com.tencent.devops.schedule.executor.JobContext
import com.tencent.devops.schedule.executor.JobHandler
import com.tencent.devops.schedule.handler.JobSystemEnv.BROADCAST_INDEX
import com.tencent.devops.schedule.handler.JobSystemEnv.BROADCAST_TOTAL
import com.tencent.devops.schedule.handler.JobSystemEnv.JOB_ID
import com.tencent.devops.schedule.handler.JobSystemEnv.JOB_PARAMETERS
import com.tencent.devops.schedule.handler.JobSystemEnv.LOG_ID
import com.tencent.devops.schedule.handler.JobSystemEnv.TRIGGER_TIME
import com.tencent.devops.schedule.handler.ShellHandler.Companion.BASH_CMD
import com.tencent.devops.schedule.k8s.ResourceLimitProperties
import com.tencent.devops.schedule.k8s.V1ConfigMap
import com.tencent.devops.schedule.k8s.V1Pod
import com.tencent.devops.schedule.k8s.buildMessage
import com.tencent.devops.schedule.k8s.configMap
import com.tencent.devops.schedule.k8s.containers
import com.tencent.devops.schedule.k8s.exec
import com.tencent.devops.schedule.k8s.limits
import com.tencent.devops.schedule.k8s.metadata
import com.tencent.devops.schedule.k8s.newEnvVar
import com.tencent.devops.schedule.k8s.newKeyToPath
import com.tencent.devops.schedule.k8s.requests
import com.tencent.devops.schedule.k8s.resources
import com.tencent.devops.schedule.k8s.spec
import com.tencent.devops.schedule.k8s.volumeMounts
import com.tencent.devops.schedule.k8s.volumes
import com.tencent.devops.schedule.pojo.job.JobExecutionResult
import com.tencent.devops.utils.jackson.toJsonString
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Container
import org.slf4j.LoggerFactory

/**
 * 处理k8s shell任务
 * 通过configmap保存脚本，使用Pod运行任务
 * */
class K8sShellHandler(
    private val client: ApiClient,
    private val namespace: String,
    private val limitProperties: ResourceLimitProperties,
) : JobHandler {
    override fun execute(context: JobContext): JobExecutionResult {
        with(context) {
            val cmd = cmdFileName ?: CMD
            val podName = "schedule-shell-$logId"
            var createdPod = false
            val api = CoreV1Api(client)
            try {
                val configMapName = "schedule-shell-$jobId-${updateTime.toEpochMilli()}"
                if (api.exec { api.readNamespacedConfigMap(configMapName, namespace, null) } == null) {
                    val configMapBody = V1ConfigMap {
                        metadata {
                            name = configMapName
                        }
                        data = mapOf(cmd to source)
                    }
                    api.createNamespacedConfigMap(namespace, configMapBody, null, null, null, null)
                    logger.info("Created configmap $configMapName")
                }
                val podBody = V1Pod {
                    metadata {
                        name = podName
                    }
                    spec {
                        containers {
                            name = logId
                            image = context.image
                            command = context.command ?: listOf(BASH_CMD, cmd)
                            workingDir = WORK_SPACE
                            setEnv(context)
                            volumeMounts {
                                name = "shell-$logId"
                                mountPath = "$WORK_SPACE/$CMD"
                                subPath = cmd
                                readOnly = true
                            }
                            resources {
                                limits(
                                    cpu = limitProperties.limitCpu,
                                    memory = limitProperties.limitMem,
                                    ephemeralStorage = limitProperties.limitStorage,
                                )
                                requests(
                                    cpu = limitProperties.requestCpu,
                                    memory = limitProperties.requestMem,
                                    ephemeralStorage = limitProperties.requestStorage,
                                )
                            }
                        }
                        volumes {
                            name = "shell-$logId"
                            configMap {
                                name = configMapName
                                items = listOf(
                                    newKeyToPath {
                                        key = cmd
                                        path = cmd
                                    },
                                )
                            }
                        }
                        restartPolicy = "Never"
                    }
                }
                api.createNamespacedPod(namespace, podBody, null, null, null, null)
                logger.info("Created pod $podName")
                createdPod = true
                var pod = api.exec { api.readNamespacedPod(podName, namespace, null) }
                var status = pod?.status?.phase.orEmpty()
                logger.info("Pod status: $status")
                while (pod != null && (status == "Running" || status == "Pending")) {
                    Thread.sleep(1000)
                    pod = api.exec { api.readNamespacedPod(podName, namespace, null) }
                    status = pod?.status?.phase.orEmpty()
                }
                logger.info("Pod status: $status")
                if (logger.isDebugEnabled) {
                    val log = api.readNamespacedPodLog(
                        podName,
                        namespace,
                        logId,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                    )
                    logger.debug("Pod log: $log")
                }
                check(pod?.status?.phase == "Succeeded") {
                    api.readNamespacedPodLog(
                        podName,
                        namespace,
                        logId,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LOG_TAIL_LINES,
                        null,
                    )
                }
            } catch (e: ApiException) {
                logger.error(e.buildMessage())
                throw e
            } finally {
                if (createdPod) {
                    api.exec { api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null) }
                    logger.info("Delete pod $podName.")
                }
            }
        }
        return JobExecutionResult.success()
    }

    private fun V1Container.setEnv(context: JobContext) {
        with(context) {
            env = listOf(
                newEnvVar {
                    name = JOB_ID
                    value = jobId
                },
                newEnvVar {
                    name = LOG_ID
                    value = logId
                },
                newEnvVar {
                    name = JOB_PARAMETERS
                    value = jobParamMap.toJsonString()
                },
                newEnvVar {
                    name = TRIGGER_TIME
                    value = triggerTime.toEpochMilli().toString()
                },
                newEnvVar {
                    name = BROADCAST_INDEX
                    value = broadcastIndex.toString()
                },
                newEnvVar {
                    name = BROADCAST_TOTAL
                    value = broadcastTotal.toString()
                },
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(K8sShellHandler::class.java)
        private const val CMD = "run.sh"
        private const val WORK_SPACE = "/data/workspace"
        private const val LOG_TAIL_LINES = 20
    }
}
