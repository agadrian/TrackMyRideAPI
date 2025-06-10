package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.AuthResponseDTO
import com.es.trackmyrideapi.dto.RefreshTokenRequest
import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.es.trackmyrideapi.exceptions.BadRequestException
import com.es.trackmyrideapi.exceptions.GeneralAppException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.exceptions.UnauthorizedException
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.service.AuthService
import com.es.trackmyrideapi.service.JwtService
import com.es.trackmyrideapi.service.RefreshTokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var refreshTokenService: RefreshTokenService

    @Autowired
    private lateinit var authService: AuthService

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Registra un nuevo usuario autenticado con Firebase y almacena sus datos en la base de datos.
     *
     * @param authHeader Header "Authorization" con el token Firebase.
     * @param userData Datos adicionales del usuario.
     * @return JWT y refresh token generados.
     * @throws AlreadyExistsException si el usuario ya está registrado.
     * @throws BadRequestException si el token no contiene email.
     */
    @PostMapping("/register")
    fun register(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody userData: UserRegistrationDTO
    ): ResponseEntity<AuthResponseDTO> {
        try {
            val token = extractToken(authHeader)
            val authResponse = authService.registerUser(token, userData)
            return ResponseEntity.ok(authResponse)
        } catch (e: Exception) {
            logger.error("Error en registro: ${e.javaClass.simpleName} - ${e.message}")
            throw e
        }
    }


    /**
     * Inicia sesión y devuelve un nuevo JWT y refresh token.
     *
     * @param authHeader Header "Authorization" con el token Firebase.
     * @return JWT y refresh token si el usuario está registrado.
     * @throws UnauthorizedException si el usuario no está registrado.
     */
    @PostMapping("/login")
    fun login(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<AuthResponseDTO> {
        try {
            val token = extractToken(authHeader)
            val authResponse = authService.loginUser(token)
            return ResponseEntity.ok(authResponse)
        } catch (e: Exception) {
            throw when (e) {
                is UnauthorizedException, is BadRequestException, is NotFoundException -> e
                else -> GeneralAppException("Unexpected error during login: ${e.message}")
            }
        }
    }


    /**
     * Valida un refresh token y devuelve un nuevo par JWT + refresh token.
     *
     * @param refreshTokenRequest Refresh token enviado por el cliente.
     * @return Nuevo JWT y refresh token.
     * @throws ResponseStatusException 401 si el token no es válido.
     */
    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refreshTokenRequest: RefreshTokenRequest
    ): ResponseEntity<AuthResponseDTO> {
        try {
            // Verificar el refresh token
            val user = refreshTokenService.verifyAndGetUser(refreshTokenRequest.refreshToken)

            // Generar el nuevo JWT
            val newJwtToken = jwtService.generateToken(user)

            // Generar un nuevo refresh token
            val newRefreshToken = refreshTokenService.generateAndStoreToken(user)

            return ResponseEntity.ok(
                AuthResponseDTO(
                    token = newJwtToken,
                    refreshToken = newRefreshToken,
                )
            )
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid refresh token")
        }
    }


    /**
     * Valida el token JWT actual y devuelve información básica del usuario autenticado.
     *
     * @param principal El JWT extraído del contexto de seguridad.
     * @return ResponseEntity con un mapa que incluye el estado del token, el email y el rol del usuario.
     */
    @GetMapping("/validate")
    fun validate(@AuthenticationPrincipal principal: Jwt): ResponseEntity<Any> {
        val email = principal.getClaimAsString("email")
        val role = principal.getClaimAsString("role")
        return ResponseEntity.ok(mapOf(
            "status" to "valid",
            "email" to email,
            "role" to role
        ))
    }


    /**
     * Endpoint de prueba para verificar la autenticación del usuario.
     *
     * @param principal El JWT extraído del contexto de seguridad.
     * @return ResponseEntity con un mensaje personalizado confirmando que el token es válido.
     */
    @GetMapping("/test-auth")
    fun testAuth(@AuthenticationPrincipal principal: Jwt): ResponseEntity<String> {
        val username = principal.getClaimAsString("username")
        return ResponseEntity.ok("Hola, $username. Tu token es válido.")
    }


    /**
     * Extrae el token JWT del encabezado Authorization.
     *
     * @param authHeader El valor del encabezado Authorization (debe tener formato "Bearer {token}").
     * @return El token JWT sin el prefijo "Bearer ".
     * @throws ResponseStatusException Si el encabezado no comienza con "Bearer ".
     */
    private fun extractToken(authHeader: String): String {
        if (!authHeader.startsWith("Bearer ")) {
            throw UnauthorizedException("Invalid authorization header")
        }
        return authHeader.removePrefix("Bearer ").trim()
    }
}



