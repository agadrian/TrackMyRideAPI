package com.es.trackmyrideapi.dto

import com.es.trackmyrideapi.model.VehicleType
import java.time.LocalDateTime

data class RouteCreateDTO(
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
    val vehicleType: VehicleType,
    val compressedPath: String // Base64 del path comprimido
)