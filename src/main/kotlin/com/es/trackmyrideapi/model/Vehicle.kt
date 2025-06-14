package com.es.trackmyrideapi.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "vehicles")
data class Vehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    var brand: String,
    var model: String,
    var year: String?,

    @Enumerated(EnumType.STRING)
    var type: VehicleType, // CAR, MOTORCYCLE, BIKE

    var fuelType: String?, // Solo para coche/moto
    var tankCapacity: Double?, // Litros
    var efficiency: Double?, // L/100km

    var notes: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val user: User
)


