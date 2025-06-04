package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.es.trackmyrideapi.exceptions.AlreadyExistsException
import com.es.trackmyrideapi.exceptions.BadRequestException
import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.UnauthorizedException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class AuthServiceTest {

    private lateinit var service: AuthService

    private val userRepository: UserRepository = mockk()
    private val refreshTokenService: RefreshTokenService = mockk()
    private val jwtService: JwtService = mockk()
    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseToken: FirebaseToken = mockk()

    private val token = "firebase_token"
    private val uid = "uid123"
    private val email = "user@example.com"
    private val username = "testuser"
    private val phone = "123456789"

    @BeforeEach
    fun setUp() {
        mockkStatic(FirebaseAuth::class)

        every { FirebaseAuth.getInstance() } returns firebaseAuth
        every { firebaseAuth.verifyIdToken(token) } returns firebaseToken
        every { firebaseToken.uid } returns uid
        every { firebaseToken.email } returns email

        service = AuthService().apply {
            this.userRepository = this@AuthServiceTest.userRepository
            this.refreshTokenService = this@AuthServiceTest.refreshTokenService
            this.jwtService = this@AuthServiceTest.jwtService
        }
    }

    @Test
    fun `loginUser should return tokens for registered user`() {
        val user = User(uid = uid, email = email, username = username, phone = phone, role = "USER", creationDate = Date.from(
            Instant.now()), isPremium = true)
        val jwt = "jwtToken"
        val refresh = "refreshToken"

        every { userRepository.findByUid(uid) } returns user
        every { jwtService.generateToken(user) } returns jwt
        every { refreshTokenService.generateAndStoreToken(user) } returns refresh

        val result = service.loginUser(token)

        assertEquals(jwt, result.token)
        assertEquals(refresh, result.refreshToken)
    }

    @Test
    fun `loginUser should throw UnauthorizedException if user not found`() {
        every { userRepository.findByUid(uid) } returns null

        assertThrows<UnauthorizedException> {
            service.loginUser(token)
        }
    }

    @Test
    fun `registerUser should save and return tokens for new user`() {
        val userData = UserRegistrationDTO(username = username, phone = phone)
        val jwt = "jwtToken"
        val refresh = "refreshToken"

        every { userRepository.existsByUid(uid) } returns false
        every { userRepository.save(any()) } answers { firstArg() }
        every { jwtService.generateToken(any()) } returns jwt
        every { refreshTokenService.generateAndStoreToken(any()) } returns refresh

        val result = service.registerUser(token, userData)

        assertEquals(jwt, result.token)
        assertEquals(refresh, result.refreshToken)
    }

    @Test
    fun `registerUser should throw AlreadyExistsException if user already exists`() {
        val userData = UserRegistrationDTO(username = username, phone = phone)

        every { userRepository.existsByUid(uid) } returns true

        assertThrows<AlreadyExistsException> {
            service.registerUser(token, userData)
        }
    }

    @Test
    fun `registerUser should throw BadRequestException if email is missing`() {
        val userData = UserRegistrationDTO(username = username, phone = phone)

        every { firebaseToken.email } returns null
        every { userRepository.existsByUid(uid) } returns false

        assertThrows<BadRequestException> {
            service.registerUser(token, userData)
        }
    }

    @Test
    fun `checkUserIsSelfOrAdmin should not throw if same user`() {
        val jwt = mockk<org.springframework.security.oauth2.jwt.Jwt>()
        every { jwt.getClaimAsString("uid") } returns uid
        every { jwt.getClaimAsString("role") } returns "USER"

        service.checkUserIsSelfOrAdmin(jwt, uid)
    }

    @Test
    fun `checkUserIsSelfOrAdmin should throw ForbiddenException if not self or admin`() {
        val jwt = mockk<org.springframework.security.oauth2.jwt.Jwt>()
        every { jwt.getClaimAsString("uid") } returns "otherUid"
        every { jwt.getClaimAsString("role") } returns "USER"

        assertThrows<ForbiddenException> {
            service.checkUserIsSelfOrAdmin(jwt, uid)
        }
    }
}
