package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.model.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class JwtServiceTest {

    private lateinit var service: JwtService
    private val jwtEncoder: JwtEncoder = mockk()

    private val uid = "uid123"
    private val email = "user@example.com"
    private val username = "testuser"
    private val phone = "123456789"

    @BeforeEach
    fun setUp() {
        service = JwtService(jwtEncoder)
    }

    @Test
    fun `generateToken should return encoded JWT token`() {
        val user = User(
            uid = uid,
            email = email,
            username = username,
            phone = phone,
            role = "USER",
            isPremium = true,
            creationDate = Date.from(Instant.now())
        )

        val tokenValue = "mocked.jwt.token"

        val jwt: Jwt = mockk()
        every { jwt.tokenValue } returns tokenValue
        every { jwtEncoder.encode(any<JwtEncoderParameters>()) } returns jwt

        val result = service.generateToken(user)

        assertEquals(tokenValue, result)
    }
}
