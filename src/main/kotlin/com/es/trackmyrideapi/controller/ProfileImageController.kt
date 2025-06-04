package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.ProfileImageRequestDTO
import com.es.trackmyrideapi.dto.ProfileImageResponseDTO
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.service.ProfileImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/profile-image")
class ProfileImageController {

    @Autowired
    private lateinit var profileImageService: ProfileImageService


    /**
     * Actualiza o crea una nueva imagen de perfil para el usuario autenticado.
     *
     * @param request DTO con la URL de la nueva imagen.
     * @param principal JWT del usuario autenticado.
     * @return ResponseEntity con los datos de la imagen de perfil actualizada.
     */
    @PutMapping
    fun updateProfileImage(
        @RequestBody request: ProfileImageRequestDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<ProfileImageResponseDTO> {
        val image = profileImageService.updateProfileImage(principal, request)
        return ResponseEntity.ok(image.toResponseDTO())
    }


    /**
     * Obtiene la imagen de perfil del usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @return ResponseEntity con los datos de la imagen de perfil.
     */
    @GetMapping
    fun getProfileImage(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<ProfileImageResponseDTO> {
        val image = profileImageService.getProfileImage(principal)
        return ResponseEntity.ok(image.toResponseDTO())
    }


    /**
     * Elimina la imagen de perfil del usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @return ResponseEntity sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping
    fun deleteProfileImage(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        profileImageService.deleteProfileImage(principal)
        return ResponseEntity.noContent().build()
    }
}