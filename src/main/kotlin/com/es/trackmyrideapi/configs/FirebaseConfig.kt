package com.es.trackmyrideapi.configs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FirebaseConfig {

    /**
     * Configuración de Firebase para inicializar FirebaseApp y exponer FirebaseAuth como un bean de Spring.
     */
    @Bean
    fun firebaseApp(): FirebaseApp {
//        val serviceAccount = this::class.java.classLoader
//            .getResourceAsStream("firebase/serviceAccount.json")
//            ?: throw IllegalStateException("Firebase config file not found")

        val serviceAccount = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")?.let { path ->
            FileInputStream(path)
        } ?: this::class.java.classLoader.getResourceAsStream("firebase/serviceAccount.json")
        ?: throw IllegalStateException("Firebase credentials not found")


        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }
    }

    @Bean
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp())
}