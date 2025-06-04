package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RouteCreateDTO
import com.es.trackmyrideapi.dto.RouteUpdateDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.repository.RouteRepository
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class RouteService {

    // Inernal para poder usarlo en los tests
    @Autowired
    internal lateinit var routeRepository: RouteRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var vehicleRepository: VehicleRepository

    @Autowired
    internal lateinit var authService: AuthService


    /**
     * Crea una nueva ruta asociada a un usuario autenticado.
     *
     * @param routeDto DTO con los datos de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Ruta creada.
     * @throws NotFoundException si el usuario o vehículo no existen.
     */
    @Transactional
    fun createRoute(
        routeDto: RouteCreateDTO,
        principal: Jwt,
    ): Route {
        val userId = principal.getClaimAsString("uid")
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User with id $userId not found") }

        val vehicle = vehicleRepository.findByTypeAndUserId(routeDto.vehicleType, userId)
            ?: throw NotFoundException("Vehicle of type ${routeDto.vehicleType} not found for user $userId")

        val route = Route(
            name = routeDto.name,
            description = routeDto.description,
            startTime = routeDto.startTime,
            endTime = routeDto.endTime,
            startPoint = routeDto.startPoint,
            endPoint = routeDto.endPoint,
            distanceKm = routeDto.distanceKm,
            movingTimeSec = routeDto.movingTimeSec,
            avgSpeed = routeDto.avgSpeed,
            maxSpeed = routeDto.maxSpeed,
            fuelConsumed = routeDto.fuelConsumed,
            efficiency = routeDto.efficiency,
            pace = routeDto.pace,
            vehicle = vehicle,
            user = user,
            compressedPath = routeDto.compressedPath
        )

        return routeRepository.save(route)
    }


    /**
     * Recupera una ruta por ID validando que el usuario sea el dueño o ADMIN.
     *
     * @param id ID de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Ruta encontrada.
     * @throws NotFoundException si la ruta no existe.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    fun getRouteById(
        id: Long,
        principal: Jwt
    ): Route {
        val route = routeRepository.findById(id)
            .orElseThrow { NotFoundException("Route with id $id not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)
        return route
    }


    /**
     * Obtiene todas las rutas asociadas al usuario autenticado.
     *
     * @param principal Token JWT del usuario autenticado.
     * @return Lista de rutas.
     * @throws NotFoundException si el usuario no existe.
     */
    fun getRoutesByUser(
        principal: Jwt
    ): List<Route> {
        val userId = principal.getClaimAsString("uid")
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User with id $userId not found") }

        return routeRepository.findByUser(user)
    }


    /**
     * Actualiza una ruta existente si el usuario tiene permisos.
     *
     * @param id ID de la ruta.
     * @param updateDto DTO con los nuevos datos.
     * @param principal Token JWT del usuario autenticado.
     * @return Ruta actualizada.
     * @throws NotFoundException si la ruta no existe.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    @Transactional
    fun updateRoute(
        id: Long,
        updateDto: RouteUpdateDTO,
        principal: Jwt
    ): Route {
        val route = routeRepository.findById(id)
            .orElseThrow { NotFoundException("Route with id $id not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        val updated = route.copy(
            name = updateDto.name ?: route.name,
            description = updateDto.description ?: route.description
        )

        return routeRepository.save(updated)
    }


    /**
     * Elimina una ruta si el usuario tiene permisos.
     *
     * @param id ID de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @throws NotFoundException si la ruta no existe.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    @Transactional
    fun deleteRoute(
        id: Long,
        principal: Jwt
    ) {
        val route = routeRepository.findById(id)
            .orElseThrow { NotFoundException("Route with id $id not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)
        routeRepository.delete(route)
    }
}