package com.es.trackmyrideapi.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@JsonIgnoreProperties("pins")
@Entity
@Table(name = "routes")
data class Route(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    val description: String?,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: LocalDateTime,

    val startPoint: String,
    val endPoint: String,

    val distanceKm: Double,
    val movingTimeSec: Long,
    val avgSpeed: Double,
    val maxSpeed: Double,

    val fuelConsumed: Double?,
    val efficiency: Double?,
    val pace: Double?,


    @Column(length = 100000)
    val compressedPath: String, // Almacena el Base64 del path comprimido

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val vehicle: Vehicle,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "uid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User,

    @OneToMany(mappedBy = "route", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pins: MutableList<RoutePin> = mutableListOf()
)
