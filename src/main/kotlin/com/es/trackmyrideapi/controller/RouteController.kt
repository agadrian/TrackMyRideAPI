package com.es.trackmyrideapi.controller


import com.es.trackmyrideapi.dto.RouteCreateDTO
import com.es.trackmyrideapi.dto.RouteResponseDTO
import com.es.trackmyrideapi.dto.RouteUpdateDTO
import com.es.trackmyrideapi.mappers.toResponseDTO
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


    /**
     * Crea una nueva ruta asociada al usuario autenticado.
     *
     * @param routeDto DTO con los datos de la nueva ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Respuesta HTTP con la ruta creada.
     */
    @PostMapping("/")
    fun createRoute(
        @RequestBody routeDto: RouteCreateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val route = routeService.createRoute(routeDto, principal)
        return ResponseEntity(route.toResponseDTO(), HttpStatus.CREATED)
    }


    /**
     * Obtiene una ruta por su ID si el usuario es el dueño o tiene rol ADMIN.
     *
     * @param id ID de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Respuesta HTTP con la ruta solicitada.
     */
    @GetMapping("/{id}")
    fun getRouteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val route = routeService.getRouteById(id, principal)
        return ResponseEntity.ok(route.toResponseDTO())
    }


    /**
     * Obtiene todas las rutas del usuario autenticado.
     *
     * @param principal Token JWT del usuario autenticado.
     * @return Lista de rutas del usuario.
     */
    @GetMapping("/user")
    fun getRoutesByUser(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<List<RouteResponseDTO>> {
        val routes = routeService.getRoutesByUser(principal).map { it.toResponseDTO() }
        return ResponseEntity.ok(routes)
    }


    /**
     * Actualiza una ruta si el usuario es el dueño o tiene rol ADMIN.
     *
     * @param id ID de la ruta a actualizar.
     * @param updateDto DTO con los datos a modificar.
     * @param principal Token JWT del usuario autenticado.
     * @return Ruta actualizada.
     */
    @PutMapping("/{id}")
    fun updateRoute(
        @PathVariable id: Long,
        @RequestBody updateDto: RouteUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteResponseDTO> {
        val route = routeService.updateRoute(id, updateDto, principal)
        return ResponseEntity.ok(route.toResponseDTO())
    }


    /**
     * Elimina una ruta si el usuario es el dueño o tiene rol ADMIN.
     *
     * @param id ID de la ruta a eliminar.
     * @param principal Token JWT del usuario autenticado.
     * @return Respuesta HTTP sin contenido.
     */
    @DeleteMapping("/{id}")
    fun deleteRoute(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        routeService.deleteRoute(id, principal)
        return ResponseEntity.noContent().build()
    }
}
