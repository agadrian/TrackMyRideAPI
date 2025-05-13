package com.es.trackmyrideapi.dto

import java.time.LocalDateTime

data class RouteResponseDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val startPoint: String,
    val endPoint: String,
    val distanceKm: Double,
    val movingTimeSec: Long,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val fuelConsumed: Double?,
    val efficiency: Double?,
    val pace: Double?,
    val vehicleId: Long,
    val userId: String
)