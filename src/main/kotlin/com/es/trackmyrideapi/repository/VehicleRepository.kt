package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.model.VehicleType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VehicleRepository : JpaRepository<Vehicle, Long> {
    fun findByName(nombre: String): Optional<Vehicle>
    fun findByUser(user: User): List<Vehicle>
    fun findByUserAndType(user: User, type: VehicleType): Vehicle?
    fun findAllByUser(user: User): List<Vehicle>
}