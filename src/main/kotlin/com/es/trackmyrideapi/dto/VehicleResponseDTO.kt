package com.es.trackmyrideapi.dto

import com.es.trackmyrideapi.model.VehicleType

data class VehicleResponseDTO(
    val id: Long,
    val name: String,
    val brand: String,
    val model: String,
    val year: String?,
    val type: VehicleType,
    val fuelType: String?,
    val tankCapacity: Double?,
    val efficiency: Double?,
    val notes: String?
)