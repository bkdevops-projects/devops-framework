package com.tencent.devops.schedule.k8s

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.models.V1ConfigMap
import io.kubernetes.client.openapi.models.V1ConfigMapVolumeSource
import io.kubernetes.client.openapi.models.V1Container
import io.kubernetes.client.openapi.models.V1EnvVar
import io.kubernetes.client.openapi.models.V1KeyToPath
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1PodSpec
import io.kubernetes.client.openapi.models.V1ResourceRequirements
import io.kubernetes.client.openapi.models.V1Volume
import io.kubernetes.client.openapi.models.V1VolumeMount

fun V1Pod.metadata(configuration: V1ObjectMeta.() -> Unit) {
    if (metadata == null) {
        metadata = V1ObjectMeta()
    }
    metadata!!.configuration()
}

fun V1ConfigMap.metadata(configuration: V1ObjectMeta.() -> Unit) {
    if (metadata == null) {
        metadata = V1ObjectMeta()
    }
    metadata!!.configuration()
}

fun V1ConfigMap(configuration: V1ConfigMap.() -> Unit): V1ConfigMap {
    return V1ConfigMap().apply(configuration)
}

fun V1Pod(configuration: V1Pod.() -> Unit): V1Pod {
    return V1Pod().apply(configuration)
}

fun V1Pod.spec(configuration: V1PodSpec.() -> Unit) {
    if (spec == null) {
        spec = V1PodSpec()
    }
    spec!!.configuration()
}

fun V1PodSpec.containers(configuration: V1Container.() -> Unit) {
    addContainersItem(V1Container().apply(configuration))
}

fun V1PodSpec.volumes(configuration: V1Volume.() -> Unit) {
    addVolumesItem(V1Volume().apply(configuration))
}

fun V1Container.volumeMounts(configuration: V1VolumeMount.() -> Unit) {
    addVolumeMountsItem(V1VolumeMount().apply(configuration))
}

fun V1Volume.configMap(configuration: V1ConfigMapVolumeSource.() -> Unit) {
    configMap(V1ConfigMapVolumeSource().apply(configuration))
}

fun newVolumeMounts(configuration: V1VolumeMount.() -> Unit): V1VolumeMount {
    return V1VolumeMount().apply(configuration)
}

fun newEnvVar(configuration: V1EnvVar.() -> Unit): V1EnvVar {
    return V1EnvVar().apply(configuration)
}

fun newKeyToPath(configuration: V1KeyToPath.() -> Unit): V1KeyToPath {
    return V1KeyToPath().apply(configuration)
}

fun V1Container.resources(configuration: V1ResourceRequirements.() -> Unit) {
    if (resources == null) {
        resources = V1ResourceRequirements()
    }
    resources!!.configuration()
}

fun V1ResourceRequirements.limits(cpu: Double, memory: Long, ephemeralStorage: Long) {
    limits(
        mapOf(
            "cpu" to Quantity("$cpu"),
            "memory" to Quantity("$memory"),
            "ephemeral-storage" to Quantity("$ephemeralStorage"),
        ),
    )
}

fun V1ResourceRequirements.requests(cpu: Double, memory: Long, ephemeralStorage: Long) {
    requests(
        mapOf(
            "cpu" to Quantity("$cpu"),
            "memory" to Quantity("$memory"),
            "ephemeral-storage" to Quantity("$ephemeralStorage"),
        ),
    )
}
