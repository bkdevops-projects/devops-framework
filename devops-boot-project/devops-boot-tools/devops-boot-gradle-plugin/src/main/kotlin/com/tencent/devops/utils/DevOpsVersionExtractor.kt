package com.tencent.devops.utils

import com.tencent.devops.DevOpsBootPlugin
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.util.jar.Attributes
import java.util.jar.JarFile

/**
 * 运行时提取自身版本号
 */
object DevOpsVersionExtractor {

    /**
     * 判断`DevOpsBoot`版本
     */
    fun extractVersion(): String? {
        val implementationVersion = this::class.java.getPackage().implementationVersion
        if (implementationVersion != null) {
            return implementationVersion
        }
        val codeSourceLocation = DevOpsBootPlugin::class.java.protectionDomain.codeSource.location
        try {
            val connection = codeSourceLocation.openConnection()
            if (connection is JarURLConnection) {
                return getImplementationVersion(connection.jarFile)
            }
            JarFile(File(codeSourceLocation.toURI())).use {
                return getImplementationVersion(it)
            }
        } catch (ex: Exception) {
            return null
        }
    }

    /**
     * 从[jarFile]的`Manifest`文件中获取版本号，需要在[jarFile]打包时写入
     */
    @Throws(IOException::class)
    private fun getImplementationVersion(jarFile: JarFile): String? {
        return jarFile.manifest.mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION)
    }
}
