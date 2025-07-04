package com.es.trackmyrideapi.dto

import java.util.Date

data class UserResponseDTO(
    val uid: String,
    val username: String,
    val email: String,
    val phone: String?,
    val isPremium: Boolean,
    val createdAt: Date
)