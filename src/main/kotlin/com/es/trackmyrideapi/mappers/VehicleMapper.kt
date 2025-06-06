package com.es.trackmyrideapi.mappers

import com.es.trackmyrideapi.dto.VehicleResponseDTO
import com.es.trackmyrideapi.model.Vehicle

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