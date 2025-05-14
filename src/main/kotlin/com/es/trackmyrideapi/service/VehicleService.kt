package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.VehicleUpdateDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.model.VehicleType
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VehicleService{

    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun crearVehiculosIniciales(uid: String): List<Vehicle> {
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        val existentes = vehicleRepository.findAllByUser(user)
        if (existentes.size >= 3) {
            throw IllegalStateException("User already has initial vehicles created")
        }

        val tipos = VehicleType.entries.toTypedArray()
        val nuevos = tipos.map { tipo ->
            Vehicle(
                name = "",
                brand = "",
                model = "",
                year = 0,
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

    fun getAllVehicles(uid: String): List<Vehicle> {
        return vehicleRepository.findByUserUid(uid)
    }

    fun getVehiculoByType(uid: String, type: VehicleType): Vehicle {
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")
        return vehicleRepository.findByUserAndType(user, type)
            ?: throw NotFoundException("Vehicle type $type not found for the user ${user.username}")
    }

    fun updateVehiculo(uid: String, type: VehicleType, dto: VehicleUpdateDTO): Vehicle {
        val vehiculo = getVehiculoByType(uid, type)
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