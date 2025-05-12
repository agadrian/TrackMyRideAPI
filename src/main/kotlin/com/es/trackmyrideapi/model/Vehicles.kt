package com.es.trackmyrideapi.model

import jakarta.persistence.*

@Entity
@Table(name = "vehicles")
data class Vehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    var brand: String,
    val model: String,
    val year: Int,

    @Enumerated(EnumType.STRING)
    val type: VehicleType, // CAR, MOTORCYCLE, BIKE

    val fuelType: String?, // Solo para coche/moto
    val tankCapacity: Double?, // Litros
    val efficiency: Double?, // L/100km

    val notes: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User
)

enum class VehicleType {
    CAR, MOTORCYCLE, BIKE
}
