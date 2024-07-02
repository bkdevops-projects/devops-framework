package com.tencent.devops.sample

import com.tencent.devops.plugin.api.Extension
import com.tencent.devops.sample.extension.PrintExtension

@Extension
class SystemIoPrintExtension : PrintExtension {
    override fun print(content: String) {
        val loaded = javaClass.classLoader.loadClass("org.bytedeco.ffmpeg.global.avformat") != null
        println("lib sdk loaded:$loaded, $content")
    }
}
