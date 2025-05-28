package com.es.trackmyrideapi.dto

data class AuthResponseDTO(
    val token: String,
    val refreshToken: String
)