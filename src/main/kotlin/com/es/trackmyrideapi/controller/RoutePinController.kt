package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.RoutePinRequestDTO
import com.es.trackmyrideapi.dto.RoutePinResponseDTO
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.service.RoutePinService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/route-pins")
class RoutePinController {

    @Autowired
    private lateinit var routePinService: RoutePinService


    /**
     * Crea un nuevo punto (pin) dentro de una ruta del usuario autenticado.
     *
     * @param pinRequestDTO Datos del pin a crear.
     * @param principal Token JWT del usuario autenticado.
     * @return El pin creado en formato DTO.
     */
    @PostMapping("/")
    fun createPin(
        @RequestBody pinRequestDTO: RoutePinRequestDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RoutePinResponseDTO> {
        val createdPin = routePinService.createPin(pinRequestDTO, principal).toResponseDTO()
        return ResponseEntity(createdPin, HttpStatus.CREATED)
    }


    /**
     * Obtiene todos los pins asociados a una ruta específica, si el usuario tiene acceso.
     *
     * @param routeId ID de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Lista de pins en formato DTO.
     */
    @GetMapping("/route/{routeId}")
    fun getPinsByRoute(
        @PathVariable routeId: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<List<RoutePinResponseDTO>> {
        val pins = routePinService.getPinsByRoute(routeId, principal).map { it.toResponseDTO() }
        return ResponseEntity.ok(pins)
    }


    /**
     * Elimina un pin específico si el usuario es dueño de la ruta o es administrador.
     *
     * @param id ID del pin a eliminar.
     * @param principal Token JWT del usuario autenticado.
     * @return Respuesta vacía con estado 204 (No Content).
     */
    @DeleteMapping("/{id}")
    fun deletePin(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        routePinService.deletePin(id, principal)
        return ResponseEntity.noContent().build()
    }
}
