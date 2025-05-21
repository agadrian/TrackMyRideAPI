package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class RouteImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    val route: Route,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now()
)