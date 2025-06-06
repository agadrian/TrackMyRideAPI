package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.UserRegistrationDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        val mockToken = mockk<FirebaseToken> {
            every { uid } returns "testUid123"
            every { email } returns "test@example.com"
        }
        every { FirebaseAuth.getInstance().verifyIdToken(any()) } returns mockToken
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `register and login user integration test`() {
        val userRegistrationDTO = UserRegistrationDTO(
            username = "testuser",
            phone = "123456789"
        )

        mockMvc.perform(
            post("/auth/register")
                .header("Authorization", "Bearer fakeToken123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationDTO))
        )
            .andExpect(status().isOk)
    }
}