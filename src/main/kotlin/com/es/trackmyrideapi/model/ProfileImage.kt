package com.es.trackmyrideapi.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "profile_images")
data class ProfileImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "image_url", nullable = false)
    var imageUrl: String,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var uploadedAt: LocalDateTime = LocalDateTime.now(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val user: User
)