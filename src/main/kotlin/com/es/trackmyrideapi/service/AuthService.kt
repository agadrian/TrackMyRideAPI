package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.AuthResponseDTO
import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.es.trackmyrideapi.exceptions.AlreadyExistsException
import com.es.trackmyrideapi.exceptions.BadRequestException
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.UnauthorizedException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class AuthService {

    @Autowired
    internal lateinit var refreshTokenService: RefreshTokenService

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var jwtService: JwtService


    /**
     * Inicia sesión verificando el token de Firebase y generando un JWT y un refresh token.
     *
     * @param token Token de Firebase enviado por el cliente.
     * @return [AuthResponseDTO] con el JWT y refresh token.
     * @throws UnauthorizedException si el usuario no está registrado.
     */
    fun loginUser(token: String): AuthResponseDTO {
        val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)

        val user = userRepository.findByUid(firebaseToken.uid)
            ?: throw UnauthorizedException("User not registered")

        val jwt = jwtService.generateToken(user)
        val refreshToken = refreshTokenService.generateAndStoreToken(user)

        return AuthResponseDTO(jwt, refreshToken)
    }


    /**
     * Registra un nuevo usuario si no existe ya en la base de datos.
     *
     * @param token Token de Firebase del usuario.
     * @param userData Datos adicionales proporcionados en el formulario de registro.
     * @return [AuthResponseDTO] con JWT y refresh token.
     * @throws AlreadyExistsException si el usuario ya existe.
     * @throws BadRequestException si el email no está presente en el token.
     */
    fun registerUser(token: String, userData: UserRegistrationDTO): AuthResponseDTO {
        val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)

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

        return AuthResponseDTO(
            token = jwtToken,
            refreshToken = refreshToken
        )
    }


    /**
     * Verifica si el usuario autenticado es el mismo que el del recurso, o tiene rol ADMIN.
     *
     * @param jwt JWT del usuario autenticado.
     * @param resourceOwnerId ID del recurso que se desea modificar/acceder.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    fun checkUserIsSelfOrAdmin(jwt: Jwt, resourceOwnerId: String) {
        val userId = jwt.getClaimAsString("uid")
        val role = jwt.getClaimAsString("role")

        if (userId != resourceOwnerId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to perform this action")
        }
    }
}