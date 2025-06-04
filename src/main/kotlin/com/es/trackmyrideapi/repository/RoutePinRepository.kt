package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RoutePin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoutePinRepository : JpaRepository<RoutePin, Long> {
    fun findByRouteId(routeId: Long): List<RoutePin>
}