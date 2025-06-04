package com.es.trackmyrideapi.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:.env.properties")
@ConfigurationProperties(prefix = "cloudinary")
class PropertyConfig{
    lateinit var cloudName: String
    lateinit var apiKey: String
    lateinit var apiSecret: String
}