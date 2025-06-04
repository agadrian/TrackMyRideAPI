package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RoutePinRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.model.RoutePin
import com.es.trackmyrideapi.repository.RoutePinRepository
import com.es.trackmyrideapi.repository.RouteRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class RoutePinService {

    @Autowired
    internal lateinit var routePinRepository: RoutePinRepository

    @Autowired
    internal lateinit var routeService: RouteService

    @Autowired
    internal lateinit var authService: AuthService


    /**
     * Crea un nuevo pin en una ruta, validando que el usuario sea dueño o administrador.
     *
     * @param pinRequestDTO Datos del pin a crear.
     * @param principal Token JWT del usuario autenticado.
     * @return El modelo del pin creado.
     */
    @Transactional
    fun createPin(
        pinRequestDTO: RoutePinRequestDTO,
        principal: Jwt
    ): RoutePin {
        val route = routeService.getRouteById(pinRequestDTO.routeId, principal)
        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        val newPin = RoutePin(
            latitude = pinRequestDTO.latitude,
            longitude = pinRequestDTO.longitude,
            title = pinRequestDTO.title,
            description = pinRequestDTO.description,
            route = route
        )

        val routeCreated = routePinRepository.save(newPin)
        return routeCreated
    }


    /**
     * Devuelve todos los pins de una ruta específica, validando que el usuario tenga permisos.
     *
     * @param routeId ID de la ruta.
     * @param principal Token JWT del usuario autenticado.
     * @return Lista de pins asociados a la ruta.
     */
    fun getPinsByRoute(
        routeId: Long,
        principal: Jwt
    ): List<RoutePin> {
        val route = routeService.getRouteById(routeId, principal)
        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        return routePinRepository.findByRouteId(routeId)
    }


    /**
     * Obtiene un pin por su ID. No realiza validación de permisos.
     *
     * @param pinId ID del pin.
     * @return El modelo del pin encontrado.
     * @throws NotFoundException si el pin no existe.
     */
    fun getPinById(pinId: Long): RoutePin {
        return routePinRepository.findById(pinId)
            .orElseThrow { NotFoundException("Pin not found") }
    }


    /**
     * Elimina un pin si el usuario es dueño del recurso o tiene rol de administrador.
     *
     * @param pinId ID del pin a eliminar.
     * @param principal Token JWT del usuario autenticado.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    fun deletePin(
        pinId: Long,
        principal: Jwt
    ) {
        val pin = getPinById(pinId)
        val route = pin.route
        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        routePinRepository.delete(pin)
    }
}