package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RouteCreateDTO
import com.es.trackmyrideapi.dto.RouteResponseDTO
import com.es.trackmyrideapi.dto.RouteUpdateDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.repository.RouteRepository
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class RouteService {

    @Autowired
    private lateinit var routeRepository: RouteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    @Transactional
    fun createRoute(routeDto: RouteCreateDTO, userId: String): RouteResponseDTO {
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

        val savedRoute = routeRepository.save(route)
        return toResponseDTO(savedRoute)
    }

    fun getRouteById(id: Long): RouteResponseDTO {
        val route = routeRepository.findById(id)
            .orElseThrow { NotFoundException("Route with id $id not found") }
        return toResponseDTO(route)
    }

    fun getRoutesByUser(userId: String): List<RouteResponseDTO> {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User with id $userId not found") }

        return routeRepository.findByUser(user).map { toResponseDTO(it) }
    }

    @Transactional
    fun updateRoute(id: Long, updateDto: RouteUpdateDTO): RouteResponseDTO {
        val route = routeRepository.findById(id)
            .orElseThrow { NotFoundException("Route with id $id not found") }

        val updatedRoute = route.copy(
            name = updateDto.name ?: route.name,
            description = updateDto.description ?: route.description
        )

        val savedRoute = routeRepository.save(updatedRoute)
        return toResponseDTO(savedRoute)
    }

    @Transactional
    fun deleteRoute(id: Long) {
        if (!routeRepository.existsById(id)) {
            throw NotFoundException("Route with id $id not found")
        }
        routeRepository.deleteById(id)
    }

    private fun toResponseDTO(route: Route): RouteResponseDTO {
        return RouteResponseDTO(
            id = route.id,
            name = route.name,
            description = route.description,
            startTime = route.startTime,
            endTime = route.endTime,
            startPoint = route.startPoint,
            endPoint = route.endPoint,
            distanceKm = route.distanceKm,
            movingTimeSec = route.movingTimeSec,
            avgSpeed = route.avgSpeed,
            maxSpeed = route.maxSpeed,
            fuelConsumed = route.fuelConsumed,
            efficiency = route.efficiency,
            pace = route.pace,
            vehiclType = route.vehicle.type,
            userId = route.user.uid
        )
    }
}