package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.VehicleResponseDTO
import com.es.trackmyrideapi.dto.VehicleUpdateDTO
import com.es.trackmyrideapi.model.VehicleType
import com.es.trackmyrideapi.model.toResponseDTO
import com.es.trackmyrideapi.service.VehicleService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vehicles")
class VehicleController(
    private val vehicleService: VehicleService
) {

    @PostMapping("/init")
    fun crearVehiculosIniciales(@AuthenticationPrincipal principal: Jwt): ResponseEntity<List<VehicleResponseDTO>> {
        val uid = principal.getClaimAsString("uid")
        val vehiculos = vehicleService.crearVehiculosIniciales(uid)
        return ResponseEntity.ok(vehiculos.map { it.toResponseDTO() })
    }

    @GetMapping
    fun getAllVehicles(@AuthenticationPrincipal principal: Jwt): ResponseEntity<List<VehicleResponseDTO>> {
        val uid = principal.getClaimAsString("uid")
        val vehicles = vehicleService.getAllVehicles(uid)
        return ResponseEntity.ok(vehicles.map { it.toResponseDTO() })
    }

    @GetMapping("/{type}")
    fun getVehiculoByType(
        @PathVariable type: VehicleType,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<VehicleResponseDTO> {
        val uid = principal.getClaimAsString("uid")
        return ResponseEntity.ok(vehicleService.getVehiculoByType(uid, type).toResponseDTO())
    }

    @PutMapping("/{type}")
    fun updateVehiculo(
        @PathVariable type: VehicleType,
        @RequestBody updateDTO: VehicleUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<VehicleResponseDTO> {
        val uid = principal.getClaimAsString("uid")
        val updated = vehicleService.updateVehiculo(uid, type, updateDTO).toResponseDTO()
        return ResponseEntity.ok(updated)
    }
}
