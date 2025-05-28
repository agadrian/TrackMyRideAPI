package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.model.VehicleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface VehicleRepository : JpaRepository<Vehicle, Long> {
    fun findByUserAndType(user: User, type: VehicleType): Vehicle?
    fun findAllByUser(user: User): List<Vehicle>
    fun findByUserUid(userId: String): List<Vehicle>
    @Query("SELECT v FROM Vehicle v WHERE v.type = :type AND v.user.uid = :userId")
    fun findByTypeAndUserId(type: VehicleType, userId: String): Vehicle?
    fun deleteByUser(user: User)
}