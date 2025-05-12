package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.repository.VehiculoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VehiculoService {

    @Autowired
    private lateinit var vehiculoRepository: VehiculoRepository

    fun createVehiculo(vehiculo: Vehicle): Vehicle {
        return vehiculoRepository.save(vehiculo)
    }

    fun getVehiculoById(id: Long): Vehicle {
        return vehiculoRepository.findById(id).orElseThrow { NotFoundException("Vehículo no encontrado") }
    }

    fun updateVehiculo(id: Long, vehiculo: Vehicle): Vehicle {
        val vehiculoExistente = getVehiculoById(id)
        vehiculoExistente.name = vehiculo.name
        vehiculoExistente.brand = vehiculo.brand
        return vehiculoRepository.save(vehiculoExistente)
    }

    fun deleteVehiculo(id: Long) {
        val vehiculoExistente = getVehiculoById(id)
        vehiculoRepository.delete(vehiculoExistente)
    }
}
