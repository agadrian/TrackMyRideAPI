package com.es.trackmyrideapi.model

import com.es.trackmyrideapi.dto.VehicleResponseDTO
import jakarta.persistence.*

@Entity
@Table(name = "vehicles")
data class Vehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    var brand: String,
    var model: String,
    var year: Int,

    @Enumerated(EnumType.STRING)
    var type: VehicleType, // CAR, MOTORCYCLE, BIKE

    var fuelType: String?, // Solo para coche/moto
    var tankCapacity: Double?, // Litros
    var efficiency: Double?, // L/100km

    var notes: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    val user: User
)

enum class VehicleType {
    CAR, MOTORCYCLE, BIKE
}


fun Vehicle.toResponseDTO(): VehicleResponseDTO {
    return VehicleResponseDTO(
        id = this.id,
        name = this.name,
        brand = this.brand,
        model = this.model,
        year = this.year,
        type = this.type,
        fuelType = this.fuelType,
        tankCapacity = this.tankCapacity,
        efficiency = this.efficiency,
        notes = this.notes
    )
}