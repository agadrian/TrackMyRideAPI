package com.es.trackmyrideapi.dto

import com.es.trackmyrideapi.model.VehicleType

data class VehicleUpdateDTO(
    val name: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val type: VehicleType? = null,
    val fuelType: String? = null,
    val tankCapacity: Double? = null,
    val efficiency: Double? = null,
    val notes: String? = null
)