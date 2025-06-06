package com.es.trackmyrideapi

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
@Configuration
@Profile("test")
class FirebaseTestConfig {

    @Bean
    @Primary
    fun firebaseApp(): FirebaseApp {
        mockkStatic(FirebaseApp::class)
        val mockApp = mockk<FirebaseApp>(relaxed = true) {
            every { name } returns FirebaseApp.DEFAULT_APP_NAME
        }
        every { FirebaseApp.getInstance() } returns mockApp
        every { FirebaseApp.getApps() } returns mutableListOf(mockApp)
        return mockApp
    }

    @Bean
    @Primary
    fun firebaseAuth(): FirebaseAuth {
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>(relaxed = true) {
            every { verifyIdToken(any()) } returns mockk {
                every { uid } returns "testUid123"
                every { email } returns "test@example.com"
            }
        }
        every { FirebaseAuth.getInstance(any()) } returns mockAuth
        return mockAuth
    }
}