package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.RouteResponseDTO
import com.es.trackmyrideapi.model.Route

fun Route.toResponseDTO(): RouteResponseDTO {
    return RouteResponseDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        startTime = this.startTime,
        endTime = this.endTime,
        startPoint = this.startPoint,
        endPoint = this.endPoint,
        distanceKm = this.distanceKm,
        movingTimeSec = this.movingTimeSec,
        avgSpeed = this.avgSpeed,
        maxSpeed = this.maxSpeed,
        fuelConsumed = this.fuelConsumed,
        efficiency = this.efficiency,
        pace = this.pace,
        vehicleType = this.vehicle.type,
        userId = this.user.uid,
        compressedRoute = this.compressedPath
    )
}

