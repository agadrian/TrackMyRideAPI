package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", referencedColumnName = "uid", nullable = false)
    val user: User,

    var token: String,

    var createdAt: LocalDateTime,

    var expiresAt: LocalDateTime
){
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
}