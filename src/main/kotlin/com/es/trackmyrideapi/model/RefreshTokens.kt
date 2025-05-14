package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userUid: String,
    val token: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusDays(7)
){
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
}