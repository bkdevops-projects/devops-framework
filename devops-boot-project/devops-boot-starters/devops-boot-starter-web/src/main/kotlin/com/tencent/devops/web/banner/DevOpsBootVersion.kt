package com.tencent.devops.web.banner

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.util.jar.Attributes
import java.util.jar.JarFile

/**
 * 获取DevOps Boot版本号工具类
 */
object DevOpsBootVersion {

    /**
     * 获取DevOps Boot版本号
     */
    fun getVersion(): String? {
        return determineDevopsBootVersion()
    }

    /**
     * 判断DevOps Boot版本号
     */
    private fun determineDevopsBootVersion(): String? {
        val implementationVersion = DevOpsBootVersion::class.java.getPackage().implementationVersion
        if (implementationVersion != null) {
            return implementationVersion
        }
        val codeSource = DevOpsBootVersion::class.java.protectionDomain.codeSource ?: return null
        val codeSourceLocation = codeSource.location
        try {
            val connection = codeSourceLocation.openConnection()
            if (connection is JarURLConnection) {
                return getImplementationVersion(connection.jarFile)
            }
            JarFile(File(codeSourceLocation.toURI())).use { jarFile ->
                return getImplementationVersion(jarFile)
            }
        } catch (ex: Exception) {
            return null
        }
    }

    @Throws(IOException::class)
    private fun getImplementationVersion(jarFile: JarFile): String? {
        return jarFile.manifest.mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION)
    }
}
