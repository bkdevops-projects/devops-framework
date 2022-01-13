package com.tencent.devops.sample

import com.tencent.devops.plugin.api.Extension
import com.tencent.devops.sample.extension.PrintExtension

@Extension
class SystemIoPrintExtension : PrintExtension {
    override fun print(content: String) {
        println(content)
    }
}
