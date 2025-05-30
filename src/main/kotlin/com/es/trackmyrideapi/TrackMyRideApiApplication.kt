package com.es.trackmyrideapi

import com.es.trackmyrideapi.configs.PropertyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PropertyConfig::class)
class TrackMyRideApiApplication

fun main(args: Array<String>) {
	runApplication<TrackMyRideApiApplication>(*args)
}
