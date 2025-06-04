package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.VehicleUpdateDTO
import com.es.trackmyrideapi.exceptions.AlreadyExistsException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.model.VehicleType
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class VehicleService{

    @Autowired
    internal lateinit var vehicleRepository: VehicleRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var authService: AuthService


    /**
     * Crea vehículos iniciales para el usuario autenticado si no tiene ya 3 o más vehículos.
     *
     * @param principal JWT del usuario autenticado.
     * @return Lista de vehículos creados o vacía si ya existen 3 o más.
     * @throws NotFoundException si el usuario no existe.
     * @throws ForbiddenException si el usuario no está autorizado para realizar esta acción.
     */
    fun crearVehiculosIniciales(
        principal: Jwt
    ): List<Vehicle> {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        val existentes = vehicleRepository.findAllByUser(user)
        if (existentes.size >= 3) {
            return emptyList()
        }

        val tipos = VehicleType.entries.toTypedArray()
        val nuevos = tipos.map { tipo ->
            Vehicle(
                name = "",
                brand = "",
                model = "",
                year = "",
                type = tipo,
                fuelType = null,
                tankCapacity = null,
                efficiency = null,
                notes = null,
                user = user
            )
        }

        return vehicleRepository.saveAll(nuevos)
    }


    /**
     * Obtiene todos los vehículos asociados al usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @return Lista de vehículos del usuario.
     * @throws NotFoundException si el usuario no existe.
     * @throws ForbiddenException si el usuario no está autorizado para realizar esta acción.
     */
    fun getAllVehicles(principal: Jwt): List<Vehicle> {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        return vehicleRepository.findByUserUid(uid)
    }


    /**
     * Obtiene un vehículo de un tipo específico asociado al usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @param type Tipo de vehículo a obtener.
     * @return Vehículo encontrado.
     * @throws NotFoundException si el usuario o el vehículo no existen.
     * @throws ForbiddenException si el usuario no está autorizado para realizar esta acción.
     */
    fun getVehiculoByType(
        principal: Jwt,
        type: VehicleType
    ): Vehicle {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        return vehicleRepository.findByUserAndType(user, type)
            ?: throw NotFoundException("Vehicle type $type not found for the user ${user.username}")
    }


    /**
     * Actualiza un vehículo específico del usuario autenticado con los datos proporcionados.
     *
     * @param principal JWT del usuario autenticado.
     * @param type Tipo de vehículo a actualizar.
     * @param dto DTO con los campos a actualizar.
     * @return Vehículo actualizado.
     * @throws NotFoundException si el usuario o el vehículo no existen.
     * @throws ForbiddenException si el usuario no está autorizado para realizar esta acción.
     */
    fun updateVehiculo(
        principal: Jwt,
        type: VehicleType,
        dto: VehicleUpdateDTO
    ): Vehicle {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        val vehiculo = vehicleRepository.findByUserAndType(user, type)
            ?: throw NotFoundException("Vehicle type $type not found for the user ${user.username}")

        dto.name?.let { vehiculo.name = it }
        dto.brand?.let { vehiculo.brand = it }
        dto.model?.let { vehiculo.model = it }
        dto.year?.let { vehiculo.year = it }
        dto.fuelType?.let { vehiculo.fuelType = it }
        dto.tankCapacity?.let { vehiculo.tankCapacity = it }
        dto.efficiency?.let { vehiculo.efficiency = it }
        dto.notes?.let { vehiculo.notes = it }

        return vehicleRepository.save(vehiculo)
    }
}