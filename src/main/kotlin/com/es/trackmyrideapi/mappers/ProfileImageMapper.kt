package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.ProfileImageResponseDTO
import com.es.trackmyrideapi.model.ProfileImage

fun ProfileImage.toResponseDTO(): ProfileImageResponseDTO{
    return ProfileImageResponseDTO(
        id = this.id,
        imageUrl = this.imageUrl,
        uploadedAt = this.uploadedAt.toString()
    )
}