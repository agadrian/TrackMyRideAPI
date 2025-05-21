package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RouteImage
import org.springframework.data.jpa.repository.JpaRepository

interface RouteImageRepository : JpaRepository<RouteImage, Long> {
    fun findByRouteId(routeId: Long): List<RouteImage>
    fun findByIdAndRouteId(id: Long, routeId: Long): RouteImage?
}