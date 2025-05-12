package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.service.VehiculoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vehiculos")
class VehiculoController {

    @Autowired
    private lateinit var vehiculoService: VehiculoService

    @PostMapping("/")
    fun createVehiculo(@RequestBody vehiculo: Vehicle): ResponseEntity<Vehicle> {
        val nuevoVehiculo = vehiculoService.createVehiculo(vehiculo)
        return ResponseEntity(nuevoVehiculo, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getVehiculoById(@PathVariable id: Long): ResponseEntity<Vehicle> {
        val vehiculo = vehiculoService.getVehiculoById(id)
        return ResponseEntity(vehiculo, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateVehiculo(@PathVariable id: Long, @RequestBody vehiculo: Vehicle): ResponseEntity<Vehicle> {
        val vehiculoUpdated = vehiculoService.updateVehiculo(id, vehiculo)
        return ResponseEntity(vehiculoUpdated, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteVehiculo(@PathVariable id: Long) {
        vehiculoService.deleteVehiculo(id)
    }
}
