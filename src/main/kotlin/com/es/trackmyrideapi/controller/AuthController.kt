package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.AuthResponseDTO
import com.es.trackmyrideapi.dto.RefreshTokenRequest
import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.es.trackmyrideapi.exceptions.*
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.service.JwtService
import com.es.trackmyrideapi.service.RefreshTokenService
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Date


@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var refreshTokenService: RefreshTokenService



    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/register")
    fun register(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody userData: UserRegistrationDTO
    ): ResponseEntity<AuthResponseDTO> {

        logger.info("======= INICIO DE REGISTRO =======")
        logger.info("Header recibido: ${authHeader.take(20)}...")

        try {
            val token = authHeader.removePrefix("Bearer ")
            val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
            //val firebaseToken = firebaseAuthService.verifyIdToken(authHeader)
            logger.info("Token decodificado correctamente para UID: ${firebaseToken.uid}")

            println("=== TOKEN DECODIFICADO ===")
            println("UID: ${firebaseToken.uid}")
            println("Email: ${firebaseToken.email}")

            if (userRepository.existsByUid(firebaseToken.uid)) {
                throw AlreadyExistsException("User already registered")
            }

            val user = User(
                uid = firebaseToken.uid,
                email = firebaseToken.email ?: throw BadRequestException("Email is required"),
                username = userData.username,
                phone = userData.phone,
                creationDate = Date.from(Instant.now()),
                isPremium = false,
                role = if (userData.username == "admin" && firebaseToken.email == "admin@admin.com") "ADMIN" else "USER"
            )

            val savedUser = userRepository.save(user)
            val jwtToken = jwtService.generateToken(savedUser)
            val refreshToken = refreshTokenService.generateAndStoreToken(savedUser)

            return ResponseEntity.ok(AuthResponseDTO(
                token = jwtToken,
                refreshToken = refreshToken,
            ))


        } catch (e: Exception) {
            logger.error("ERROR en registro: ${e.javaClass.simpleName} - ${e.message}")
            throw e
        }
    }


    @PostMapping("/login")
    fun login(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<AuthResponseDTO> {
        try{

            val token = extractToken(authHeader)
            val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)

            val user = userRepository.findByUid(firebaseToken.uid)
                ?: throw UnauthorizedException("User not registered")

            val jwt = jwtService.generateToken(user)
            val refreshToken = refreshTokenService.generateAndStoreToken(user)

            return ResponseEntity.ok(
                AuthResponseDTO(
                    token = jwt,
                    refreshToken = refreshToken,
                )
            )
        }catch (e: Exception){
            if (e is UnauthorizedException || e is BadRequestException || e is NotFoundException) {
                throw e
            }

            throw GeneralAppException("Unexpected error during login: ${e.message}")
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

            // Generar un nuevo refresh token (si quieres puedes regenerarlo cada vez o solo uno por usuario)
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



