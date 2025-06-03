package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RoutePinRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.RoutePins
import com.es.trackmyrideapi.repository.RoutePinRepository
import com.es.trackmyrideapi.repository.RouteRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoutePinService {

    @Autowired
    private lateinit var routePinRepository: RoutePinRepository

    @Autowired
    private lateinit var routeRepository: RouteRepository

    @Transactional
    fun createPin(pinRequestDTO: RoutePinRequestDTO): RoutePins {
        val route = routeRepository.findById(pinRequestDTO.routeId)
            .orElseThrow { NotFoundException("Route with id ${pinRequestDTO.routeId} not found") }

        val newPin = RoutePins(
            latitude = pinRequestDTO.latitude,
            longitude = pinRequestDTO.longitude,
            title = pinRequestDTO.title,
            description = pinRequestDTO.description,
            route = route
        )

        return routePinRepository.save(newPin)
    }

    fun getPinsByRoute(routeId: Long): List<RoutePins> {
        return routePinRepository.findByRouteId(routeId)
    }

    fun getPinById(pinId: Long): RoutePins {
        return routePinRepository.findById(pinId)
            .orElseThrow { NotFoundException("Pin not found") }
    }

    fun deletePin(pinId: Long) {
        val pin = getPinById(pinId)
        routePinRepository.delete(pin)
    }

}