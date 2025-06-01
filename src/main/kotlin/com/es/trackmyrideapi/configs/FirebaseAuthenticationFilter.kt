package com.es.trackmyrideapi.configs

import com.es.trackmyrideapi.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*


/**
 * Filtro personalizado que intercepta peticiones para validar tokens de Firebase antes de pasar al filtro JWT.
 *
 * Solo aplica la validación Firebase para el endpoint `/auth/register` y '/auth/login'.
 * Si el token es un JWT tradicional, lo deja continuar con el filtro de seguridad por defecto.
 */
@Component
class FirebaseAuthenticationFilter() : OncePerRequestFilter() {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            // Si es un JWT válido (formato estándar), no hacer nada, deja que siga al filtro de JWT
            if (isJwtToken(token) && !isFirebaseToken(token)) {
                filterChain.doFilter(request, response)
                return
            }

            if (isFirebaseToken(token) && (path == "/auth/register" || path == "/auth/login")){

                // Si es un Firebase Token, validamos y autenticamos
                try {
                    val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                    val user = userRepository.findByUid(firebaseToken.uid)

                    user?.let {
                        val auth = FirebaseAuthenticationToken(
                            it.uid,
                            null,
                            listOf(SimpleGrantedAuthority("ROLE_${it.role ?: "USER"}"))
                        )
                        SecurityContextHolder.getContext().authentication = auth
                    }
                } catch (e: Exception) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase Token")
                    return
                }

                // Continuar con el flujo de filtros
                filterChain.doFilter(request, response)
                return
            }
        }

        // Si no es un JWT ni un token Firebase válido, continuar sin autenticar
        filterChain.doFilter(request, response)
    }

    private fun isJwtToken(token: String): Boolean {
        // JWT tokens tienen estructura de 3 partes separadas por '.'
        return token.matches(Regex("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\$"))
    }

    private fun isFirebaseToken(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false

            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            payload.contains("\"iss\":\"https://securetoken.google.com/")
        } catch (e: Exception) {
            false
        }
    }
}

class FirebaseAuthenticationToken(
    private val uid: String,
    credentials: Any?,
    authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials() = null
    override fun getPrincipal() = uid
}