package com.es.trackmyrideapi.security

import com.es.trackmyrideapi.configs.FirebaseAuthenticationFilter
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Configuration
@EnableWebSecurity
class SecurityConfig{
    @Autowired
    private lateinit var rsaKeys: RSAKeysProperties

    @Autowired
    private lateinit var firebaseAuthFilter: FirebaseAuthenticationFilter


    // REGISTER Y LOGIN -> FIREBASE.  Refresh publico.
    @Bean
    @Order(1)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .securityMatcher("/auth/register", "/auth/login", "/auth/refresh")
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }
            .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }

    // Cadena para el resto de rutas (usando JWT local firmado)
    @Bean
    @Order(2)
    fun securedRoutes(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests { it ->
                // Usuarios \\
                it.requestMatchers(HttpMethod.GET, "/users/").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/users/{id}").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.PUT, "users/setPremium").authenticated()
                it.requestMatchers(HttpMethod.GET, "users/isPremium").authenticated()
                it.requestMatchers(HttpMethod.PUT, "users/changeSubscriptionAdmin/{id}").hasRole("ADMIN")


                // Vehiculos \\
                it.requestMatchers(HttpMethod.POST, "/vehicles/init").authenticated()
                it.requestMatchers(HttpMethod.GET, "/vehicles").authenticated()
                it.requestMatchers(HttpMethod.GET, "/vehicles/{type}").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/vehicles/{type}").authenticated()

                // Rutas \\
                it.requestMatchers(HttpMethod.POST, "/routes/").authenticated()
                it.requestMatchers(HttpMethod.GET, "/routes/{id}").authenticated()
                it.requestMatchers(HttpMethod.GET, "/routes/user").authenticated()
                it.requestMatchers(HttpMethod.PUT, "/routes/{id}").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/routes/{id}").authenticated()

                // Imagenes ruta \\
                it.requestMatchers(HttpMethod.POST, "/routes/{routeId}/images").authenticated()
                it.requestMatchers(HttpMethod.GET, "/routes/{routeId}/images").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/routes/{routeId}/images/{imageId}").authenticated()


                // Imagenes perfil \\
                it.requestMatchers(HttpMethod.PUT, "/users/profile-image").authenticated()
                it.requestMatchers(HttpMethod.GET, "/users/profile-image").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/users/profile-image").authenticated()


                // Pines de rutas \\
                it.requestMatchers(HttpMethod.POST, "/route-pins/").authenticated()
                it.requestMatchers(HttpMethod.GET, "/route-pins/route/{routeId}").authenticated()
                it.requestMatchers(HttpMethod.DELETE, "/route-pins/{id}").authenticated()


                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }



    /**
     * Metodo para codificar un JWT
     */
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeys.publicKey).privateKey(rsaKeys.privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }


    /**
     * Metodo para decodificar un JWT
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey).build()
    }


    /**
     * Este bean define cómo se deben extraer las autoridades (roles) desde el JWT recibido.
     * Es necesario para que Spring Security sepa qué permisos (GrantedAuthority) tiene el usuario autenticado.
     */
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        val authoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
            setAuthorityPrefix("ROLE_")
            setAuthoritiesClaimName("role")
        }
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter)
        return converter
    }


    /**
     * DEV
     */
    @Component
    class DebugFilter : OncePerRequestFilter() {

        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            logger.info("======= INICIO DE PETICIÓN =======")
            logger.info("URL: ${request.requestURI}")
            logger.info("Método: ${request.method}")
            logger.info("########################################\n\n\n\n")
            filterChain.doFilter(request, response)
        }
    }
}


