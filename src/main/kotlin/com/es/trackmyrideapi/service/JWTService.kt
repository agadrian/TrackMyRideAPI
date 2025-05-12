package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.model.User
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
) {

    /**
     * Genera un JWT personalizado para un usuario autenticado, usando JwtEncoder de Spring Security.
     */
    fun generateToken(user: User): String {
        val now = Instant.now()
        val expiry = now.plus(1, ChronoUnit.HOURS)

        val claims = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "role" to user.role,
            "username" to user.username,
            "phone" to user.phone,
            "isPremium" to user.isPremium,
            "creationDate" to user.creationDate.toString()
        )

        val jwtClaims = JwtClaimsSet.builder()
            .subject(user.uid)
            .issuedAt(now)
            .expiresAt(expiry)
            .claims { it.putAll(claims) }
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaims)).tokenValue
    }
}