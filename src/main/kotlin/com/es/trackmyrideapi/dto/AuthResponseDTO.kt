package com.es.trackmyrideapi.dto

data class AuthResponseDTO(
    val token: String,
    val uid: String,
    val email: String,
    val role: String,
    val username: String
)