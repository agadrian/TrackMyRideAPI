package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RoutePinRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.Route
import com.es.trackmyrideapi.model.RoutePin
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.RoutePinRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class RoutePinServiceTest {

    private lateinit var service: RoutePinService

    private val routePinRepository: RoutePinRepository = mockk()
    private val routeService: RouteService = mockk()
    private val authService: AuthService = mockk()
    private val principal: Jwt = mockk()

    @BeforeEach
    fun setUp() {
        service = RoutePinService().apply {
            this.routePinRepository = this@RoutePinServiceTest.routePinRepository
            this.routeService = this@RoutePinServiceTest.routeService
            this.authService = this@RoutePinServiceTest.authService
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

    private fun mockRoute(user: User) = Route(
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
    fun `createPin should save and return new pin`() {
        val user = mockUser()
        val route = mockRoute(user = user)
        val dto = RoutePinRequestDTO(
            latitude = 40.0,
            longitude = -3.0,
            title = "Pin Title",
            description = "Pin Description",
            routeId = route.id
        )

        every { routeService.getRouteById(route.id, principal) } returns route
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routePinRepository.save(any()) } answers { firstArg() }

        val result = service.createPin(dto, principal)

        assertEquals(dto.latitude, result.latitude)
        assertEquals(dto.longitude, result.longitude)
        assertEquals(dto.title, result.title)
        assertEquals(dto.description, result.description)
        assertEquals(route, result.route)
    }

    @Test
    fun `getPinsByRoute should return list of pins`() {
        val user = mockUser()
        val route = mockRoute(user = user)
        val pins = listOf(
            RoutePin(1L, 40.0, -3.0, "Pin1", "Desc1", route),
            RoutePin(2L, 41.0, -4.0, "Pin2", "Desc2", route)
        )

        every { routeService.getRouteById(route.id, principal) } returns route
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routePinRepository.findByRouteId(route.id) } returns pins

        val result = service.getPinsByRoute(route.id, principal)

        assertEquals(2, result.size)
        assertEquals("Pin1", result[0].title)
    }

    @Test
    fun `getPinById should return pin if exists`() {
        val user = mockUser()
        val route = mockRoute(user = user)
        val pin = RoutePin(1L, 40.0, -3.0, "Title", "Desc", route)

        every { routePinRepository.findById(1L) } returns Optional.of(pin)

        val result = service.getPinById(1L)

        assertEquals("Title", result.title)
    }

    @Test
    fun `getPinById should throw NotFoundException if not exists`() {
        every { routePinRepository.findById(1L) } returns Optional.empty()

        assertThrows<NotFoundException> {
            service.getPinById(1L)
        }
    }

    @Test
    fun `deletePin should delete pin if authorized`() {
        val user = mockUser()
        val route = mockRoute(user = user)
        val pin = RoutePin(1L, 40.0, -3.0, "Title", "Desc", route)

        every { routePinRepository.findById(1L) } returns Optional.of(pin)
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs
        every { routePinRepository.delete(pin) } just Runs

        service.deletePin(1L, principal)

        verify { routePinRepository.delete(pin) }
    }
}
