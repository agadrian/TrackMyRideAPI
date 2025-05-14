package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.model.RefreshToken
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.RefreshTokenRepository
import com.es.trackmyrideapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class RefreshTokenService{

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    // verifica si el refresh token es válido y devuelve el usuario asociado. Si el token ha expirado o no es válido, lanza una excepción.
    fun verifyAndGetUser(refreshToken: String): User {
        val token = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        // Aquí puedes verificar si el token está expirado o no
        if (token.isExpired()) {
            throw IllegalArgumentException("Refresh token has expired")
        }

        return userRepository.findByUid(token.userUid)
            ?: throw IllegalArgumentException("User not found for the given refresh token")
    }

    // Mgenera un refresh token para el usuario y lo guarda en la base de datos.
    fun generateAndStoreToken(user: User): String {
        // Crear el refresh token (deberías añadir validación aquí)
        val refreshToken = generateTokenForUser(user)

        // Guardar el refresh token en base de datos
        refreshTokenRepository.save(RefreshToken(userUid = user.uid, token = refreshToken))

        return refreshToken
    }

    // Método para generar un nuevo refresh token
    private fun generateTokenForUser(user: User): String {
        // Aquí deberías generar un refresh token. Usualmente se hace con JWT o algún algoritmo similar.
        return UUID.randomUUID().toString()  // Puedes sustituirlo por la lógica real de generación de refresh token.
    }
}