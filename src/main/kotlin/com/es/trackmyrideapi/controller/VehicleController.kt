package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.VehicleResponseDTO
import com.es.trackmyrideapi.dto.VehicleUpdateDTO
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.model.VehicleType
import com.es.trackmyrideapi.service.VehicleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vehicles")
class VehicleController {

    @Autowired
    private lateinit var vehicleService: VehicleService

    /**
     * Crea vehículos iniciales para el usuario autenticado.
     *
     * @param principal El JWT del usuario autenticado.
     * @return ResponseEntity con la lista de vehículos creados (vacía si ya existen).
     */
    @PostMapping("/init")
    fun crearVehiculosIniciales(@AuthenticationPrincipal principal: Jwt): ResponseEntity<List<VehicleResponseDTO>> {
        val vehicles = vehicleService.crearVehiculosIniciales(principal)
        return ResponseEntity.ok(vehicles.map { it.toResponseDTO() })
    }


    /**
     * Obtiene todos los vehículos asociados al usuario autenticado.
     *
     * @param principal El JWT del usuario autenticado.
     * @return ResponseEntity con la lista de vehículos del usuario.
     */
    @GetMapping
    fun getAllVehicles(@AuthenticationPrincipal principal: Jwt): ResponseEntity<List<VehicleResponseDTO>> {
        val vehicles = vehicleService.getAllVehicles(principal)
        return ResponseEntity.ok(vehicles.map { it.toResponseDTO() })
    }


    /**
     * Obtiene un vehículo del usuario autenticado según su tipo.
     *
     * @param type El tipo de vehículo a obtener.
     * @param principal El JWT del usuario autenticado.
     * @return ResponseEntity con el vehículo encontrado.
     * @throws NotFoundException Si el vehículo de ese tipo no existe para el usuario.
     */
    @GetMapping("/{type}")
    fun getVehiculoByType(
        @PathVariable type: VehicleType,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<VehicleResponseDTO> {
        return ResponseEntity.ok(vehicleService.getVehiculoByType(principal, type).toResponseDTO())
    }


    /**
     * Actualiza un vehículo del usuario autenticado según su tipo.
     *
     * @param type El tipo de vehículo a actualizar.
     * @param updateDTO Los datos con los campos a actualizar.
     * @param principal El JWT del usuario autenticado.
     * @return ResponseEntity con el vehículo actualizado.
     * @throws NotFoundException Si el vehículo de ese tipo no existe para el usuario.
     */
    @PutMapping("/{type}")
    fun updateVehiculo(
        @PathVariable type: VehicleType,
        @RequestBody updateDTO: VehicleUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<VehicleResponseDTO> {
        val updated = vehicleService.updateVehiculo(principal, type, updateDTO).toResponseDTO()
        return ResponseEntity.ok(updated)
    }
}
