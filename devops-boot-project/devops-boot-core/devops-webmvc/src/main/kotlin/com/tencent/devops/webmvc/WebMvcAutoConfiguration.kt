package com.tencent.devops.webmvc

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:common-webmvc.properties")
class WebMvcAutoConfiguration