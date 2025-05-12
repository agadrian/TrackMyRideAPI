package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RutaRepository : JpaRepository<Route, Long> {
    fun findByUser(usuario: User): List<Route>
    fun findByName(nombre: String): Optional<Route>
}