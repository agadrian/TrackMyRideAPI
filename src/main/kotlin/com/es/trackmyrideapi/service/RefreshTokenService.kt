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
        logger.info("Verificando refresh token")

        // Primero buscar el token para validarlo
        val token = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Refresh token inválido")

        if (token.isExpired()) {
            logger.info("Refresh token expirado, borrando...")
            refreshTokenRepository.delete(token)
            throw IllegalArgumentException("Refresh token expirado")
        }

        // Ahora que sabemos que es válido, eliminar para evitar reutilización
        val deletedRows = refreshTokenRepository.deleteByToken(refreshToken)
        if (deletedRows == 0) {
            throw IllegalArgumentException("Refresh token ya fue usado")
        }

        // Obtener usuario
        val user = userRepository.findByUid(token.userUid)
            ?: throw IllegalArgumentException("Usuario no encontrado para el refresh token")

        return user
    }

    /**
     * Genera un nuevo refresh token, elimina los anteriores y guarda el nuevo en la base de datos.
     */
    @Transactional
    fun generateAndStoreToken(user: User): String {

        val newToken = generateTokenForUser()
        val now = LocalDateTime.now()

        val existingToken = refreshTokenRepository.findByUserUid(user.uid)

        if (existingToken != null) {
            logger.info("Actualizando token existente para usuario ${user.uid}")
            existingToken.token = newToken
            existingToken.createdAt = now
            existingToken.expiresAt = now.plusDays(7)
            refreshTokenRepository.save(existingToken)
        } else {
            logger.info("Creando nuevo token para usuario ${user.uid}")
            val refreshToken = RefreshToken(
                userUid = user.uid,
                token = newToken,
                createdAt = now,
                expiresAt = now.plusDays(7)
            )
            refreshTokenRepository.save(refreshToken)
        }
        return newToken
    }

    // Método para generar un nuevo refresh token
    private fun generateTokenForUser(): String {
        logger.info("genratetokenforuser")
        return UUID.randomUUID().toString()
    }
}