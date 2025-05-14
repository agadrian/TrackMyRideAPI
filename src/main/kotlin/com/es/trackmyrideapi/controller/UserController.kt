package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.UserResponseDTO
import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    /**
     * Obtener listado de todos los usuarios (ADMIN)
     */
    @GetMapping("/")
    fun getAllUsuarios(): ResponseEntity<List<UserResponseDTO>> {
        val usuarios = userService.getAllUsuarios()
        return ResponseEntity(usuarios, HttpStatus.OK)
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    fun getUsuarioById(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {
        // Obtener el uid del token JWT
        val uidFromToken = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        // Si es ADMIN, puede acceder a cualquier usuario
        // Si no es ADMIN, solo puede acceder a su propio usuario
        if (role != "ADMIN" && uidFromToken != id) {
            throw ForbiddenException("You don't have permission to access this source")
        }

        val usuario = userService.getUsuarioById(id)
        return ResponseEntity(usuario, HttpStatus.OK)
    }

    /**
     * Actualizar usuario por ID
     */
    @PutMapping("/{id}")
    fun updateUsuario(
        @PathVariable id: String,
        @RequestBody usuario: UserUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {

        val uidFromToken = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        if (role != "ADMIN" && uidFromToken != id) {
            throw ForbiddenException("You don't have permission to update this user")
        }

        val updatedUser = userService.updateUsuario(id, usuario)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }

    /**
     * Eliminar usuario por ID
     */
    @DeleteMapping("/{id}")
    fun deleteUsuario(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Void> {
        val role = principal.getClaimAsString("role")

        // Solo ADMIN puede eliminar usuarios
        if (role != "ADMIN") {
            throw ForbiddenException("Only admins can delete users")
        }

        userService.deleteUsuario(id)
        return ResponseEntity.noContent().build()
    }
}