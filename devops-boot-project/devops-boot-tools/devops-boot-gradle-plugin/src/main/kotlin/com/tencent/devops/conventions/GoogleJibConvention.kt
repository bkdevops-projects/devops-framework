package com.tencent.devops.conventions

import com.google.cloud.tools.jib.gradle.BuildImageTask
import com.google.cloud.tools.jib.gradle.JibExtension
import com.google.cloud.tools.jib.gradle.JibPlugin
import com.tencent.devops.enums.AssemblyMode
import com.tencent.devops.utils.findPropertyOrEmpty
import com.tencent.devops.utils.resolveAssemblyMode
import org.gradle.api.Project
import org.slf4j.LoggerFactory

/**
 * Google Jib 插件配置
 */
class GoogleJibConvention {

    fun apply(project: Project) {
        if (project.findPropertyOrEmpty("devops.jib") != "true") {
            logger.debug("devops.jib is not true , google convention is disabled")
            return
        }

        if (resolveAssemblyMode(project) != AssemblyMode.KUBERNETES) {
            logger.warn("Google Jib Convention is failed , project must use KUBERNETES Mode")
            return
        }

        with(project) {
            pluginManager.apply(JibPlugin::class.java)
            logger.debug("Apply GoogleJibConvention success!")
        }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(GoogleJibConvention::class.java)
    }
}
