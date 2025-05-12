package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.repository.RutaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class RutaService {

    @Autowired
    private lateinit var rutaRepository: RutaRepository

    @Autowired
    private lateinit var usuarioService: UsuarioService

    fun createRuta(ruta: Route, authentication: Authentication): Route {
        val usuario = usuarioService.getUsuarioById(authentication.name)
        ruta.user = usuario
        return rutaRepository.save(ruta)
    }

    fun getAllRutas(authentication: Authentication): List<Route> {
        val usuario = usuarioService.getUsuarioById(authentication.name)
        return rutaRepository.findByUser(usuario)
    }

    fun getRutaById(id: String, authentication: Authentication): Route {
        val ruta = rutaRepository.findById(id.toLong()).orElseThrow { NotFoundException("Ruta no encontrada") }
        val usuario = usuarioService.getUsuarioById(authentication.name)
        if (ruta.user.uid != usuario.uid) {
            throw ForbiddenException("No tienes acceso a esta ruta")
        }
        return ruta
    }

    fun updateRuta(id: String, ruta: Route, authentication: Authentication): Route {
        val rutaExistente = getRutaById(id, authentication)
        rutaExistente.name = ruta.name
        return rutaRepository.save(rutaExistente)
    }

    fun deleteRuta(id: String, authentication: Authentication) {
        val ruta = getRutaById(id, authentication)
        rutaRepository.delete(ruta)
    }
}
