package com.es.trackmyrideapi.service

import java.time.LocalDateTime
import com.es.trackmyrideapi.dto.RouteImageRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.model.RouteImage
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.RouteImageRepository
import com.es.trackmyrideapi.repository.RouteRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

class RouteImageServiceTest {

    private lateinit var routeImageService: RouteImageService

    private val routeRepository: RouteRepository = mockk()
    private val routeImageRepository: RouteImageRepository = mockk()
    private val cloudinaryService: CloudinaryService = mockk()
    private val authService: AuthService = mockk()
    private val principal: Jwt = mockk()

    @BeforeEach
    fun setUp() {
        routeImageService = RouteImageService().apply {
            this.routeRepository = this@RouteImageServiceTest.routeRepository
            this.routeImageRepository = this@RouteImageServiceTest.routeImageRepository
            this.cloudinaryService = this@RouteImageServiceTest.cloudinaryService
            this.authService = this@RouteImageServiceTest.authService
        }
    }

    private fun createMockUser() = User(
        uid = "user123",
        username = "TestUser",
        email = "test@example.com",
        phone = "123456789",
        role = "USER",
        creationDate = Date(),
        isPremium = false,
        profileImage = null
    )

    private fun createMockRoute(user: User) = Route(
        id = 1L,
        name = "Test Route",
        description = "Desc",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1),
        startPoint = "A",
        endPoint = "B",
        distanceKm = 10.0,
        movingTimeSec = 3600,
        avgSpeed = 10.0,
        maxSpeed = 15.0,
        fuelConsumed = 2.0,
        efficiency = 5.0,
        pace = 6.0,
        compressedPath = "abc123",
        vehicle = mockk(),
        user = user
    )

    @Test
    fun `addImageToRoute should add and return new RouteImage`() {
        val user = createMockUser()
        val route = createMockRoute(user)
        val request = RouteImageRequestDTO(imageUrl = "http://test.com/image.jpg")
        val savedImage = RouteImage(id = 1L, route = route, imageUrl = request.imageUrl)

        every { routeRepository.findById(route.id ) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routeImageRepository.save(any()) } returns savedImage

        val result = routeImageService.addImageToRoute(route.id , request, principal)

        assertEquals(savedImage, result)
        verify { authService.checkUserIsSelfOrAdmin(principal, user.uid) }
        verify { routeImageRepository.save(any()) }
    }

    @Test
    fun `addImageToRoute should throw NotFoundException if route not found`() {
        every { routeRepository.findById(999L) } returns Optional.empty()

        val request = RouteImageRequestDTO(imageUrl = "http://test.com/image.jpg")

        assertThrows<NotFoundException> {
            routeImageService.addImageToRoute(999L, request, principal)
        }
    }

    @Test
    fun `getImagesForRoute should return list of images`() {
        val user = createMockUser()
        val route = createMockRoute(user)
        val imageList = listOf(
            RouteImage(id = 1L, route = route, imageUrl = "http://1.com"),
            RouteImage(id = 2L, route = route, imageUrl = "http://2.com")
        )

        every { routeRepository.findById(route.id ) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routeImageRepository.findByRouteId(route.id ) } returns imageList

        val result = routeImageService.getImagesForRoute(route.id , principal)

        assertEquals(2, result.size)
        assertEquals("http://1.com", result[0].imageUrl)
        verify { authService.checkUserIsSelfOrAdmin(principal, user.uid) }
    }

    @Test
    fun `getImagesForRoute should throw NotFoundException if route not found`() {
        every { routeRepository.findById(999L) } returns Optional.empty()

        assertThrows<NotFoundException> {
            routeImageService.getImagesForRoute(999L, principal)
        }
    }

    @Test
    fun `deleteImage should remove image from DB and Cloudinary`() {
        val user = createMockUser()
        val route = createMockRoute(user)
        val image = RouteImage(id = 1L, route = route, imageUrl = "http://image.com")

        every { routeRepository.findById(route.id ) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routeImageRepository.findByIdAndRouteId(image.id , route.id ) } returns image
        every { cloudinaryService.deleteFromCloudinary(image.imageUrl) } just Runs
        every { routeImageRepository.delete(image) } just Runs

        routeImageService.deleteImage(route.id , image.id , principal)

        verify { cloudinaryService.deleteFromCloudinary(image.imageUrl) }
        verify { routeImageRepository.delete(image) }
    }

    @Test
    fun `deleteImage should throw NotFoundException if route not found`() {
        every { routeRepository.findById(123L) } returns Optional.empty()

        assertThrows<NotFoundException> {
            routeImageService.deleteImage(123L, 1L, principal)
        }
    }

    @Test
    fun `deleteImage should throw NotFoundException if image not found for route`() {
        val user = createMockUser()
        val route = createMockRoute(user)

        every { routeRepository.findById(route.id ) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routeImageRepository.findByIdAndRouteId(99L, route.id ) } returns null

        assertThrows<NotFoundException> {
            routeImageService.deleteImage(route.id , 99L, principal)
        }
    }
}
