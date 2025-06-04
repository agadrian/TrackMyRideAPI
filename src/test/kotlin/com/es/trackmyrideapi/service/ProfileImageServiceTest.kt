package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.ProfileImageRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.ProfileImage
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.ProfileImageRepository
import com.es.trackmyrideapi.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProfileImageServiceTest {

    private lateinit var service: ProfileImageService

    private val userRepository: UserRepository = mockk()
    private val profileImageRepository: ProfileImageRepository = mockk()
    private val cloudinaryService: CloudinaryService = mockk()
    private val authService: AuthService = mockk()
    private val principal: Jwt = mockk()

    @BeforeEach
    fun setUp() {
        service = ProfileImageService().apply {
            this.userRepository = this@ProfileImageServiceTest.userRepository
            this.profileImageRepository = this@ProfileImageServiceTest.profileImageRepository
            this.cloudinaryService = this@ProfileImageServiceTest.cloudinaryService
            this.authService = this@ProfileImageServiceTest.authService
        }
    }

    private fun mockUser(uid: String = "user123") = User(
        uid = uid,
        username = "Test User",
        email = "test@example.com",
        phone = "123456789",
        role = "USER",
        creationDate = Date(),
        isPremium = false,
        profileImage = null
    )

    @Test
    fun `updateProfileImage should update existing image and delete old one from Cloudinary`() {
        val user = mockUser()
        val oldImage = ProfileImage(user = user, imageUrl = "oldUrl", uploadedAt = LocalDateTime.now().minusDays(1))
        val request = ProfileImageRequestDTO("newUrl")

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns oldImage
        every { cloudinaryService.deleteFromCloudinary("oldUrl") } just Runs
        every { profileImageRepository.save(any()) } answers { firstArg() }

        val result = service.updateProfileImage(principal, request)

        assertEquals("newUrl", result.imageUrl)
        assertNotNull(result.uploadedAt)
        verify { cloudinaryService.deleteFromCloudinary("oldUrl") }
    }

    @Test
    fun `updateProfileImage should create new image if none exists`() {
        val user = mockUser()
        val request = ProfileImageRequestDTO("newImageUrl")

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns null
        every { profileImageRepository.save(any()) } answers { firstArg() }

        val result = service.updateProfileImage(principal, request)

        assertEquals("newImageUrl", result.imageUrl)
        verify(exactly = 0) { cloudinaryService.deleteFromCloudinary(any()) }
    }

    @Test
    fun `updateProfileImage should throw NotFoundException if user not found`() {
        every { principal.getClaimAsString("uid") } returns "missingUid"
        every { userRepository.findByUid("missingUid") } returns null

        val request = ProfileImageRequestDTO("url")

        assertThrows<NotFoundException> {
            service.updateProfileImage(principal, request)
        }
    }

    @Test
    fun `getProfileImage should return image if exists`() {
        val user = mockUser()
        val profileImage = ProfileImage(user = user, imageUrl = "imgUrl", uploadedAt = LocalDateTime.now())

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns profileImage

        val result = service.getProfileImage(principal)

        assertEquals("imgUrl", result.imageUrl)
    }

    @Test
    fun `getProfileImage should throw NotFoundException if user not found`() {
        every { principal.getClaimAsString("uid") } returns "noUser"
        every { userRepository.findByUid("noUser") } returns null

        assertThrows<NotFoundException> {
            service.getProfileImage(principal)
        }
    }

    @Test
    fun `getProfileImage should throw NotFoundException if image not found`() {
        val user = mockUser()
        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns null

        assertThrows<NotFoundException> {
            service.getProfileImage(principal)
        }
    }

    @Test
    fun `deleteProfileImage should delete image from Cloudinary and repository`() {
        val user = mockUser()
        val image = ProfileImage(user = user, imageUrl = "imgUrl", uploadedAt = LocalDateTime.now())

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns image
        every { cloudinaryService.deleteFromCloudinary("imgUrl") } just Runs
        every { profileImageRepository.delete(image) } just Runs

        service.deleteProfileImage(principal)

        verify { cloudinaryService.deleteFromCloudinary("imgUrl") }
        verify { profileImageRepository.delete(image) }
    }

    @Test
    fun `deleteProfileImage should throw NotFoundException if image not found`() {
        val user = mockUser()
        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { profileImageRepository.findByUserUid(user.uid) } returns null

        assertThrows<NotFoundException> {
            service.deleteProfileImage(principal)
        }
    }

    @Test
    fun `deleteProfileImage should throw NotFoundException if user not found`() {
        every { principal.getClaimAsString("uid") } returns "nope"
        every { userRepository.findByUid("nope") } returns null

        assertThrows<NotFoundException> {
            service.deleteProfileImage(principal)
        }
    }
}
