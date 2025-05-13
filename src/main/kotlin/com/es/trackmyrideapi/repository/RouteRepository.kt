package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RouteRepository : JpaRepository<Route, Long> {
    fun findByUser(user: User): List<Route>
    fun existsByIdAndUser(id: Long, user: User): Boolean
}