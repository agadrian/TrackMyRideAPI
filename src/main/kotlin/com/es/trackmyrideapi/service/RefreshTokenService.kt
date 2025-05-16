package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.model.RefreshToken
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.RefreshTokenRepository
import com.es.trackmyrideapi.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


@Service
class RefreshTokenService{

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private val logger = LoggerFactory.getLogger(this::class.java)

    // verifica si el refresh token es válido y devuelve el usuario asociado. Si el token ha expirado o no es válido, lanza una excepción.
    @Transactional
    fun verifyAndGetUser(refreshToken: String): User {
        logger.info("Obtienenod token")
        val token = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        logger.info("Token obtenido: $token")


        if (token.isExpired()) {
            // Limpieza de token
            logger.info("Token expirado")
            refreshTokenRepository.delete(token)
            throw IllegalArgumentException("Refresh token has expired")
        }

        logger.info("Llamando a userrepository.findbyuid")

        // Obtener el usuario
        val user = userRepository.findByUid(token.userUid)
            ?: throw IllegalArgumentException("User not found for the given refresh token")

        logger.info("User: $user")

        // Eliminar el token usado para evitar reutilización
        logger.info("Borrando el token: $token")
        refreshTokenRepository.delete(token)

        return user
    }

    /**
     * Genera un nuevo refresh token, elimina los anteriores y guarda el nuevo en la base de datos.
     */
    @Transactional
    fun generateAndStoreToken(user: User): String {
        // Eliminar tokens anteriores para ese usuario
        logger.info("Eliminando tokens anteriores")
        refreshTokenRepository.deleteAllByUserUid(user.uid)

        // Generar nuevo token
        val newToken = generateTokenForUser()
        logger.info("Nuevo token: $newToken")

        // Usar el mismo en created y expires para evitar desfase de milisegundos
        val now = LocalDateTime.now()

        // Guardar en base de datos
        val refreshToken = RefreshToken(
            userUid = user.uid,
            token = newToken,
            createdAt = now,
            expiresAt = now.plusDays(7)
        )

        logger.info("Nueva entidad refreshtoken: createdat ${refreshToken.createdAt}. token: $newToken")

        refreshTokenRepository.save(refreshToken)

        return newToken
    }

    // Método para generar un nuevo refresh token
    private fun generateTokenForUser(): String {
        logger.info("genratetokenforuser")
        return UUID.randomUUID().toString()
    }
}