package com.es.trackmyrideapi.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.Date

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
    val creationDate: Date,

    @Column(name = "is_premium")
    var isPremium: Boolean,

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var profileImage: ProfileImage? = null,

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val refreshTokens: MutableList<RefreshToken> = mutableListOf()
)
