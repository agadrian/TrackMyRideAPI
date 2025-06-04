package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.RouteImageResponseDTO
import com.es.trackmyrideapi.model.RouteImage

fun RouteImage.toResponseDTO(): RouteImageResponseDTO{
    return RouteImageResponseDTO(
        id = this.id,
        imageUrl = this.imageUrl,
        uploadedAt = this.uploadedAt.toString()
    )
}