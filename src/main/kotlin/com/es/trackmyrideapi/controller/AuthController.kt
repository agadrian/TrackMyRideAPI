package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.AuthResponseDTO
import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.es.trackmyrideapi.exceptions.AlreadyExistsException
import com.es.trackmyrideapi.exceptions.BadRequestException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UsuarioRepository
import com.es.trackmyrideapi.service.JwtService
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime


@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtService: JwtService,
    private val usuarioRepository: UsuarioRepository
) {

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

            if (usuarioRepository.existsByUid(firebaseToken.uid)) {
                throw AlreadyExistsException("User already registered")
            }

            val user = User(
                uid = firebaseToken.uid,
                email = firebaseToken.email ?: throw BadRequestException("Email is required"),
                username = userData.username,
                phone = userData.phone,
                creationDate = LocalDateTime.now(),
                isPremium = false,
                role = "USER"
            )

            val savedUser = usuarioRepository.save(user)
            val jwtToken = jwtService.generateToken(savedUser)

            return ResponseEntity.ok(AuthResponseDTO(
                token = jwtToken,
                uid = savedUser.uid,
                email = savedUser.email,
                role = savedUser.role,
                username = savedUser.username
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
        val token = extractToken(authHeader)
        val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)

        val user = usuarioRepository.findByUid(firebaseToken.uid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not registered")

        val jwt = jwtService.generateToken(user)

        return ResponseEntity.ok(
            AuthResponseDTO(
                token = jwt,
                uid = user.uid,
                email = user.email,
                role = user.role,
                username = user.username
            )
        )
    }


    private fun extractToken(authHeader: String): String {
        if (!authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header")
        }
        return authHeader.removePrefix("Bearer ").trim()
    }
}



