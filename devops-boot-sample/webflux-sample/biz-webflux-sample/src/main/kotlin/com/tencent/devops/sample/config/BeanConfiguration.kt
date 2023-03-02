package com.tencent.devops.sample.config

import com.tencent.devops.sample.filter.LogWebFilter
import com.tencent.devops.sample.filter.SampleHandlerFilterFunction
import com.tencent.devops.sample.handler.SampleHandler
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val beans = beans {
    bean<SampleHandler>()
    bean<LogWebFilter>()
    bean<SampleHandlerFilterFunction>()
    bean {
        RouteConfiguration(ref(), ref()).router()
    }
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        beans.initialize(applicationContext)
    }
}
