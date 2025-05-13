package com.es.trackmyrideapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "routes")
data class Route(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    val description: String?,

    val startTime: LocalDateTime,
    val endTime: LocalDateTime,

    val startPoint: String,
    val endPoint: String,

    val distanceKm: Double,
    val movingTimeSec: Long,
    val avgSpeed: Double,
    val maxSpeed: Double,

    val fuelConsumed: Double?,
    val efficiency: Double?, // Real durante la ruta
    val pace: Double?, // Min/km


    @Column(length = 100000)
    val compressedPath: String, // Almacena el Base64 del path comprimido

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    val vehicle: Vehicle,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "uid")
    var user: User
)
