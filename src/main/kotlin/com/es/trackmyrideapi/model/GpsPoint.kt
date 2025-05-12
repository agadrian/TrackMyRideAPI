package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "gps_points")
data class GpsPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    val route: Route
)
