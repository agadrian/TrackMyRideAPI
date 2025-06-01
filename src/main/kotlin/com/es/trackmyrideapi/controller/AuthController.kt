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
     *  Este endpoint recibe un refresh token, lo verifica mediante el servicio refreshTokenService, genera un nuevo JWT y un nuevo refresh token y los devuelve al cliente.
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
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
        }
    }




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


    @GetMapping("/test-auth")
    fun testAuth(@AuthenticationPrincipal principal: Jwt): ResponseEntity<String> {
        val username = principal.getClaimAsString("username")
        return ResponseEntity.ok("Hola, $username. Tu token es válido.")
    }




    private fun extractToken(authHeader: String): String {
        if (!authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header")
        }
        return authHeader.removePrefix("Bearer ").trim()
    }
}



