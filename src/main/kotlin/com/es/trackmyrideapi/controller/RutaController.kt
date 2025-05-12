package com.es.trackmyrideapi.controller


import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.service.RutaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rutas")
class RutaController {

    @Autowired
    private lateinit var rutaService: RutaService

    /**
     * Registrar una nueva ruta
     */
    @PostMapping("/")
    fun createRuta(@RequestBody ruta: Route, authentication: Authentication): ResponseEntity<Route> {
        val nuevaRuta = rutaService.createRuta(ruta, authentication)
        return ResponseEntity(nuevaRuta, HttpStatus.CREATED)
    }

    /**
     * Obtener todas las rutas
     */
    @GetMapping("/")
    fun getAllRutas(authentication: Authentication): ResponseEntity<List<Route>> {
        val rutas = rutaService.getAllRutas(authentication)
        return ResponseEntity(rutas, HttpStatus.OK)
    }

    /**
     * Obtener ruta por ID
     */
    @GetMapping("/{id}")
    fun getRutaById(@PathVariable id: String, authentication: Authentication): ResponseEntity<Route> {
        val ruta = rutaService.getRutaById(id, authentication)
        return ResponseEntity(ruta, HttpStatus.OK)
    }

    /**
     * Actualizar ruta por ID
     */
    @PutMapping("/{id}")
    fun updateRuta(@PathVariable id: String, @RequestBody ruta: Route, authentication: Authentication): ResponseEntity<Route> {
        val updatedRuta = rutaService.updateRuta(id, ruta, authentication)
        return ResponseEntity(updatedRuta, HttpStatus.OK)
    }

    /**
     * Eliminar ruta por ID
     */
    @DeleteMapping("/{id}")
    fun deleteRuta(@PathVariable id: String, authentication: Authentication) {
        rutaService.deleteRuta(id, authentication)
    }
}
