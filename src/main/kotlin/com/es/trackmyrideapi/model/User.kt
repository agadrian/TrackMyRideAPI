package com.es.trackmyrideapi.model

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val creationDate: LocalDateTime,

    @Column(name = "is_premium")
    var isPremium: Boolean,
)
