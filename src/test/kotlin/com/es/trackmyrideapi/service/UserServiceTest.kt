package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.exceptions.FirebaseException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

class UserServiceTest {

    private lateinit var userService: UserService
    private val userRepository: UserRepository = mockk()
    private val authService: AuthService = mockk()
    private val firebaseAuth: FirebaseAuth = mockk()

    @BeforeEach
    fun setup() {
        userService = UserService().apply {
            this.userRepository = this@UserServiceTest.userRepository
            this.authService = this@UserServiceTest.authService
        }

        // Mock singleton static getInstance call for FirebaseAuth
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuth
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun createMockUser(
        uid: String = "user123",
        username: String = "MockUser",
        isPremium: Boolean = false,
        email: String = "mock@example.com"
    ) = User(
        uid = uid,
        username = username,
        email = email,
        phone = "123456789",
        role = "USER",
        creationDate = Date(),
        isPremium = isPremium,
        profileImage = null
    )

    private fun createMockJwt(uid: String = "user123", role: String = "USER"): Jwt {
        val jwt = mockk<Jwt>()
        every { jwt.getClaimAsString("uid") } returns uid
        every { jwt.getClaimAsString("role") } returns role
        return jwt
    }

    @Test
    fun `getAllUsuarios returns list of users`() {
        val users = listOf(createMockUser(), createMockUser(uid = "user456"))
        every { userRepository.findAll() } returns users

        val result = userService.getAllUsuarios()

        assertEquals(2, result.size)
        verify { userRepository.findAll() }
    }

    @Test
    fun `getUsuarioById returns user when authorized`() {
        val user = createMockUser()
        val jwt = createMockJwt()
        every { authService.checkUserIsSelfOrAdmin(jwt, user.uid) } just Runs
        every { userRepository.findById(user.uid) } returns Optional.of(user)

        val result = userService.getUsuarioById(jwt, user.uid)

        assertEquals(user, result)
        verify { authService.checkUserIsSelfOrAdmin(jwt, user.uid) }
        verify { userRepository.findById(user.uid) }
    }

    @Test
    fun `getUsuarioById throws NotFoundException when user not found`() {
        val jwt = createMockJwt()
        every { authService.checkUserIsSelfOrAdmin(jwt, "unknown") } just Runs
        every { userRepository.findById("unknown") } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            userService.getUsuarioById(jwt, "unknown")
        }
        assertTrue(exception.message!!.contains("User id unknown not found"))
    }

    @Test
    fun `updateUsuario updates user when authorized`() {
        val jwt = createMockJwt()
        val originalUser = createMockUser()
        val updateDto = UserUpdateDTO(username = "UpdatedUser", phone = "999999999")

        every { authService.checkUserIsSelfOrAdmin(jwt, originalUser.uid) } just Runs
        every { userRepository.findById(originalUser.uid) } returns Optional.of(originalUser)
        every { userRepository.save(any()) } answers { firstArg() }

        val updatedUser = userService.updateUsuario(jwt, originalUser.uid, updateDto)

        assertEquals("UpdatedUser", updatedUser.username)
        assertEquals("999999999", updatedUser.phone)
        verify { userRepository.save(updatedUser) }
    }

    @Test
    fun `deleteUsuario deletes user successfully`() {
        val jwt = createMockJwt()
        val user = createMockUser()

        every { authService.checkUserIsSelfOrAdmin(jwt, user.uid) } just Runs
        every { userRepository.findById(user.uid) } returns Optional.of(user)
        every { firebaseAuth.deleteUser(user.uid) } just Runs
        every { userRepository.deleteById(user.uid) } just Runs

        userService.deleteUsuario(jwt, user.uid)

        verify { firebaseAuth.deleteUser(user.uid) }
        verify { userRepository.deleteById(user.uid) }
    }

    @Test
    fun `deleteUsuario throws FirebaseException on Firebase deletion error`() {
        val jwt = createMockJwt()
        val user = createMockUser()

        every { authService.checkUserIsSelfOrAdmin(jwt, user.uid) } just Runs
        every { userRepository.findById(user.uid) } returns Optional.of(user)
        every { firebaseAuth.deleteUser(user.uid) } throws RuntimeException("Firebase error")

        val exception = assertThrows(FirebaseException::class.java) {
            userService.deleteUsuario(jwt, user.uid)
        }
        assertTrue(exception.message!!.contains("Error deleting user in Firebase"))
    }

    @Test
    fun `isUserPremium returns correct premium status`() {
        val user = createMockUser(isPremium = true)
        val jwt = createMockJwt()

        every { userRepository.findById(user.uid) } returns Optional.of(user)

        val result = userService.isUserPremium(jwt)

        assertTrue(result)
    }

    @Test
    fun `setUserPremium updates premium status`() {
        val jwt = createMockJwt()
        val user = createMockUser(isPremium = false)
        every { authService.checkUserIsSelfOrAdmin(jwt, user.uid) } just Runs
        every { userRepository.findById(user.uid) } returns Optional.of(user)
        every { userRepository.save(any()) } answers { firstArg() }

        val updatedUser = userService.setUserPremium(jwt, true, user.uid)

        assertTrue(updatedUser.isPremium)
        verify { userRepository.save(updatedUser) }
    }

    @Test
    fun `updateProfileImage updates image`() {
        val jwt = createMockJwt()
        val user = createMockUser()
        every { userRepository.findById(user.uid) } returns Optional.of(user)
        every { userRepository.save(any()) } answers { firstArg() }

        val updatedUser = userService.updateProfileImage(jwt, "http://image.url/pic.jpg")

        assertNotNull(updatedUser.profileImage)
        assertEquals("http://image.url/pic.jpg", updatedUser.profileImage?.imageUrl)
    }
}
