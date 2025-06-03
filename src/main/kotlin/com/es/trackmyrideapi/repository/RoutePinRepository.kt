package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RoutePins
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoutePinRepository : JpaRepository<RoutePins, Long> {
    fun findByRouteId(routeId: Long): List<RoutePins>
}