package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.RoutePinResponseDTO
import com.es.trackmyrideapi.model.RoutePin


fun RoutePin.toResponseDTO(): RoutePinResponseDTO {
    return RoutePinResponseDTO(
        id = this.id,
        latitude = this.latitude,
        longitude = this.longitude,
        title = this.title,
        description = this.description,
        routeId = this.route.id
    )
}
