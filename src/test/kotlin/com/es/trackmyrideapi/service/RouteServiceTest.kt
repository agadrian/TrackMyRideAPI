package com.es.trackmyrideapi.service

// src/test/kotlin/com/es/trackmyrideapi/service/RouteServiceTest.kt

import com.es.trackmyrideapi.exceptions.ForbiddenException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.RouteRepository
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(properties = ["spring.profiles.active=test"])
class RouteServiceTest {

    private lateinit var routeService: RouteService
    private val routeRepository: RouteRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val vehicleRepository: VehicleRepository = mockk()
    private val authService: AuthService = mockk()

    @BeforeEach
    fun setUp() {
        routeService = RouteService().apply {
            this.routeRepository = this@RouteServiceTest.routeRepository
            this.userRepository = this@RouteServiceTest.userRepository
            this.vehicleRepository = this@RouteServiceTest.vehicleRepository
            this.authService = this@RouteServiceTest.authService
        }
    }

    @Test
    fun `getRouteById returns route when user is authorized`() {
        // Given
        val user = createMockUser()
        val route = createMockRoute(user)
        val jwt: Jwt = mockk()
        every { jwt.getClaimAsString("uid") } returns "user123"

        every { routeRepository.findById(1L) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(jwt, "user123") } just Runs

        // When
        val result = routeService.getRouteById(1L, jwt)

        // Then
        assertEquals(route, result)
        verify { routeRepository.findById(1L) }
        verify { authService.checkUserIsSelfOrAdmin(jwt, "user123") }
    }

    @Test
    fun `getRouteById throws NotFoundException when route does not exist`() {
        val jwt: Jwt = mockk()
        every { routeRepository.findById(999L) } returns Optional.empty()

        val exception = assertThrows(NotFoundException::class.java) {
            routeService.getRouteById(999L, jwt)
        }

        assertTrue(exception.message!!.contains("Route with id 999 not found"))
    }

    @Test
    fun `getRouteById throws ForbiddenException if user unauthorized`() {
        val user = createMockUser()
        val route = createMockRoute(user)
        val jwt: Jwt = mockk()

        every { routeRepository.findById(1L) } returns Optional.of(route)
        every { authService.checkUserIsSelfOrAdmin(jwt, "user123") } throws ForbiddenException("Forbidden")

        assertThrows(ForbiddenException::class.java) {
            routeService.getRouteById(1L, jwt)
        }
    }

    private fun createMockUser(uid: String = "user123") = User(
        uid = uid,
        username = "MockUser",
        email = "mock@example.com",
        phone = null,
        role = "USER",
        creationDate = Date(),
        isPremium = false,
        profileImage = null
    )

    fun createMockRoute(user: User) = Route(
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
}
