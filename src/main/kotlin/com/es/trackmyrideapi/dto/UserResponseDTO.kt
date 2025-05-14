package com.es.trackmyrideapi.dto

import com.es.trackmyrideapi.model.User
import java.time.LocalDateTime

data class UserResponseDTO(
    val uid: String,
    val username: String,
    val email: String,
    val phone: String?,
    val isPremium: Boolean,
    val createdAt: LocalDateTime
)

fun User.toResponseDTO(): UserResponseDTO {
    return UserResponseDTO(
        uid = this.uid,
        username = this.username,
        email = this.email,
        phone = this.phone,
        isPremium = this.isPremium,
        createdAt = this.creationDate
    )
}