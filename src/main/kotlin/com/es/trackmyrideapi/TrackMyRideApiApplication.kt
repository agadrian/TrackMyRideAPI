package com.es.trackmyrideapi

import com.es.trackmyrideapi.configs.PropertyConfig
import com.es.trackmyrideapi.security.RSAKeysProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PropertyConfig::class, RSAKeysProperties::class)
class TrackMyRideApiApplication

fun main(args: Array<String>) {
	runApplication<TrackMyRideApiApplication>(*args)
}
