package com.es.trackmyrideapi.controller


import com.es.trackmyrideapi.dto.RouteCreateDTO
import com.es.trackmyrideapi.dto.RouteResponseDTO
import com.es.trackmyrideapi.dto.RouteUpdateDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.service.RouteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/routes")
class RouteController {

    @Autowired
    private lateinit var routeService: RouteService

    @PostMapping("/")
    fun createRoute(
        @RequestBody routeDto: RouteCreateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val userId = principal.getClaimAsString("uid")
        val createdRoute = routeService.createRoute(routeDto, userId)
        return ResponseEntity(createdRoute, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getRouteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val route = routeService.getRouteById(id)

        // Solo ADMIN o el dueño puede ver la ruta
        if (role != "ADMIN" && route.userId != userId) {
            throw ForbiddenException("You don't have permission to access this route")
        }

        return ResponseEntity(route, HttpStatus.OK)
    }


    @GetMapping("/user")
    fun getRoutesByUser(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<List<RouteResponseDTO>> {
        val userId = principal.getClaimAsString("uid")
        val routes = routeService.getRoutesByUser(userId)
        return ResponseEntity(routes, HttpStatus.OK)
    }


    @PutMapping("/{id}")
    fun updateRoute(
        @PathVariable id: Long,
        @RequestBody updateDto: RouteUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val existingRoute = routeService.getRouteById(id)

        if (role != "ADMIN" && existingRoute.userId != userId) {
            throw ForbiddenException("You don't have permission to update this route")
        }

        val updatedRoute = routeService.updateRoute(id, updateDto)
        return ResponseEntity(updatedRoute, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteRoute(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val existingRoute = routeService.getRouteById(id)

        if (role != "ADMIN" && existingRoute.userId != userId) {
            throw ForbiddenException("You don't have permission to delete this route")
        }

        routeService.deleteRoute(id)
        return ResponseEntity.noContent().build()
    }
}

