package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userUid: String,
    val token: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
){
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
}