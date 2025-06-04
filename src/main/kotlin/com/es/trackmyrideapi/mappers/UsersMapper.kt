package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.UserResponseDTO
import com.es.trackmyrideapi.model.User

fun User.toResponseDTO(): UserResponseDTO {
    return UserResponseDTO(
        uid = this.uid,
        email = this.email,
        username = this.username,
        phone = this.phone,
        isPremium = this.isPremium,
        createdAt = this.creationDate
    )
}