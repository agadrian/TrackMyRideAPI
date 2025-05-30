package com.es.trackmyrideapi.configs

import com.cloudinary.Cloudinary
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CloudinaryConfig(private val propertyConfig: PropertyConfig) {

    @Bean
    fun cloudinary(): Cloudinary {
        val config = mapOf(
            "cloud_name" to propertyConfig.cloudName,
            "api_key" to propertyConfig.apiKey,
            "api_secret" to propertyConfig.apiSecret
        )
        return Cloudinary(config)
    }
}