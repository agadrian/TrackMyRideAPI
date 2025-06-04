package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.model.Vehicle
import com.es.trackmyrideapi.model.VehicleType
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

class VehicleServiceTest {

    private lateinit var vehicleService: VehicleService

    private val vehicleRepository: VehicleRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val authService: AuthService = mockk()
    private val principal: Jwt = mockk()

    @BeforeEach
    fun setUp() {
        vehicleService = VehicleService().apply {
            this.vehicleRepository = this@VehicleServiceTest.vehicleRepository
            this.userRepository = this@VehicleServiceTest.userRepository
            this.authService = this@VehicleServiceTest.authService
        }
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

    private fun createMockVehicle(
        user: User,
        id: Long = 1L,
        name: String = "My Vehicle",
        brand: String = "BrandX",
        model: String = "ModelY",
        year: String? = "2023",
        type: VehicleType = VehicleType.CAR,
        fuelType: String? = "Gasoline",
        tankCapacity: Double? = 50.0,
        efficiency: Double? = 7.5,
        notes: String? = "Some notes"
    ) = Vehicle(
        id = id,
        name = name,
        brand = brand,
        model = model,
        year = year,
        type = type,
        fuelType = fuelType,
        tankCapacity = tankCapacity,
        efficiency = efficiency,
        notes = notes,
        user = user
    )

    @Test
    fun `crearVehiculosIniciales return emptylist if 3 or more`() {
        val user = createMockUser()

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { vehicleRepository.findAllByUser(user) } returns List(3) { createMockVehicle(user) }
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs

        val result = vehicleService.crearVehiculosIniciales(principal)

        assertTrue(result.isEmpty())
        verify { authService.checkUserIsSelfOrAdmin(principal, user.uid) }
    }

    @Test
    fun `crearVehiculosIniciales throw NotFoundException if user not exists`() {
        every { principal.getClaimAsString("uid") } returns "unknown"
        every { userRepository.findByUid("unknown") } returns null

        assertThrows(NotFoundException::class.java) {
            vehicleService.crearVehiculosIniciales(principal)
        }
    }

    @Test
    fun `getAllVehicles return all vehicles`() {
        val user = createMockUser()
        val vehicles = listOf(
            createMockVehicle(user),
            createMockVehicle(user, id = 2L, type = VehicleType.MOTORCYCLE)
        )

        every { principal.getClaimAsString("uid") } returns user.uid
        every { userRepository.findByUid(user.uid) } returns user
        every { vehicleRepository.findByUserUid(user.uid) } returns vehicles
        every { authService.checkUserIsSelfOrAdmin(principal, user.uid) } just Runs

        val result = vehicleService.getAllVehicles(principal)

        assertEquals(2, result.size)
        verify { authService.checkUserIsSelfOrAdmin(principal, user.uid) }
    }
}


