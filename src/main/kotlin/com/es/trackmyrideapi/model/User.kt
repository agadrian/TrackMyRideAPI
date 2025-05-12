package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    val uid: String, // ID proporcionado por Firebase (UID)

    var username: String,
    var email: String,
    var phone: String?,

    var role: String = "USER",

    @Column(name = "created_at")
    val creationDate: LocalDateTime,

    @Column(name = "is_premium")
    var isPremium: Boolean,
)
