package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.RoutePinRequestDTO
import com.es.trackmyrideapi.dto.RoutePinResponseDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.model.toResponseDTO
import com.es.trackmyrideapi.service.RoutePinService
import com.es.trackmyrideapi.service.RouteService
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

    @Autowired
    private lateinit var routeService: RouteService

    @PostMapping("/")
    fun createPin(
        @RequestBody pinRequestDTO: RoutePinRequestDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RoutePinResponseDTO> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        // Validar que la ruta existe y pertenece al usuario (o admin)
        val route = routeService.getRouteById(pinRequestDTO.routeId)

        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to add a pin to this route")
        }

        val createdPin = routePinService.createPin(pinRequestDTO).toResponseDTO()
        return ResponseEntity(createdPin, HttpStatus.CREATED)
    }

    @GetMapping("/route/{routeId}")
    fun getPinsByRoute(@PathVariable routeId: Long,  @AuthenticationPrincipal principal: Jwt): ResponseEntity<List<RoutePinResponseDTO>> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val route = routeService.getRouteById(routeId)

        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to view pins for this route")
        }

        val pins = routePinService.getPinsByRoute(routeId).map { it.toResponseDTO() }
        return ResponseEntity.ok(pins)
    }

    @DeleteMapping("/{id}")
    fun deletePin(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        // Se comprueba que el pin existe y pertenece al usuario o que es admin
        val pin = routePinService.getPinById(id)

        val route = routeService.getRouteById(pin.route.id)

        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to delete this pin")
        }

        routePinService.deletePin(id)

        return ResponseEntity.noContent().build()
    }
}
