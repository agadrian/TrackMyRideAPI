package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.AuthResponseDTO
import com.es.trackmyrideapi.dto.UserResponseDTO
import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.service.AuthService
import com.es.trackmyrideapi.service.JwtService
import com.es.trackmyrideapi.service.RefreshTokenService
import com.es.trackmyrideapi.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var refreshTokenService: RefreshTokenService

    private val logger: Logger = Logger.getLogger(this.javaClass.name)

    /**
     * Obtener listado de todos los usuarios (requiere rol ADMIN).
     *
     * @return lista de usuarios en formato DTO.
     */
    @GetMapping("/")
    fun getAllUsuarios(): ResponseEntity<List<UserResponseDTO>> {
        val usuarios = userService.getAllUsuarios().map {it.toResponseDTO()}
        return ResponseEntity(usuarios, HttpStatus.OK)
    }

    /**
     * Obtener datos de un usuario específico.
     *
     * @param id ID del usuario.
     * @param principal JWT del usuario autenticado.
     * @return los datos del usuario.
     */
    @GetMapping("/{id}")
    fun getUsuarioById(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {
        val usuario = userService.getUsuarioById(principal, id)
        return ResponseEntity(usuario.toResponseDTO(), HttpStatus.OK)
    }

    /**
     * Actualizar información de un usuario.
     *
     * @param id ID del usuario a actualizar.
     * @param usuario datos actualizados.
     * @param principal JWT del usuario autenticado.
     * @return usuario actualizado.
     */
    @PutMapping("/{id}")
    fun updateUsuario(
        @PathVariable id: String,
        @RequestBody usuario: UserUpdateDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {
        val updatedUser = userService.updateUsuario(principal, id, usuario)
        return ResponseEntity(updatedUser.toResponseDTO(), HttpStatus.OK)
    }


    /**
     * Eliminar un usuario.
     *
     * @param id ID del usuario a eliminar.
     * @param principal JWT del usuario autenticado.
     * @return respuesta vacía con status 204.
     */
    @DeleteMapping("/{id}")
    fun deleteUsuario(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        userService.deleteUsuario(principal, id)
        return ResponseEntity.noContent().build()
    }


    /**
     * Consultar si el usuario autenticado es premium.
     *
     * @param principal JWT del usuario autenticado.
     * @return mapa con clave "isPremium" y valor booleano.
     */
    @GetMapping("/isPremium")
    fun isUserPremium(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Map<String, Boolean>> {
        val isPremium = userService.isUserPremium(principal)
        return ResponseEntity.ok(mapOf("isPremium" to isPremium))
    }


    /**
     * Establecer el estado premium del usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @return usuario actualizado.
     */
    @PutMapping("/setPremium")
    fun setUserPremium(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<AuthResponseDTO> {
        val userId = principal.getClaimAsString("uid")
        val updatedUser = userService.setUserPremium(principal, true, userId)

        val newToken = jwtService.generateToken(updatedUser)
        val refreshToken = refreshTokenService.generateAndStoreToken(updatedUser)

        val authResponseDTO = AuthResponseDTO(
            token = newToken,
            refreshToken = refreshToken,
        )

        return ResponseEntity.ok(authResponseDTO)
    }

    /**
     * Cambiar el estado premium de un usuario como ADMIN.
     * Alterna entre true y false.
     *
     * @param id ID del usuario objetivo.
     * @param principal JWT del usuario autenticado (debe ser ADMIN).
     * @return usuario actualizado.
     */
    @PutMapping("/changeSubscriptionAdmin/{id}")
    fun changeSubscriptionAdmin(
        @PathVariable id: String,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {
        val role = principal.getClaimAsString("role")
        if (role != "ADMIN") {
            throw ForbiddenException("You don't have permission to access this resource")
        }
        val currentUser = userService.getUsuarioById(principal, id)
        val newPremiumStatus = !currentUser.isPremium

        val updatedUser = userService.setUserPremium(principal, newPremiumStatus, id)
        return ResponseEntity.ok(updatedUser.toResponseDTO())
    }


    /**
     * Actualizar la imagen de perfil del usuario autenticado.
     *
     * @param id ID del usuario (puede omitirse si es el propio).
     * @param body mapa con clave "imageUrl" y la URL de la nueva imagen.
     * @param principal JWT del usuario autenticado.
     * @return usuario actualizado.
     */
    @PutMapping("/{id}/profileImage")
    fun updateProfileImage(
        @PathVariable id: String,
        @RequestBody body: Map<String, String>,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<UserResponseDTO> {
        val imageUrl = body["imageUrl"] ?: throw IllegalArgumentException("Missing imageUrl")
        val updatedUser = userService.updateProfileImage(principal, imageUrl)
        return ResponseEntity.ok(updatedUser.toResponseDTO())
    }
}