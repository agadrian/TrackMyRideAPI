package com.es.trackmyrideapi.dto

// Este es el objeto que recibirá el cliente al hacer una solicitud POST al endpoint /refresh. Contiene el refresh token.
data class RefreshTokenRequest(
    val refreshToken: String
)