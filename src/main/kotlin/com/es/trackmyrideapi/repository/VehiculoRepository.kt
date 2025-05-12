package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VehiculoRepository : JpaRepository<Vehicle, Long> {
    fun findByName(nombre: String): Optional<Vehicle>
}