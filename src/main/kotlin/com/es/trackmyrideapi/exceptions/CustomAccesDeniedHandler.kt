package com.es.trackmyrideapi.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        val errorMessage = ErrorMessage(
            status = HttpStatus.FORBIDDEN.value(),
            message = "Access Denied: No tienes permisos para acceder a este recurso.",
            path = request.requestURI
        )

        // Respuesta como JSON
        response.contentType = "application/json"
        response.status = HttpStatus.FORBIDDEN.value()

        // Convertimos el ErrorMessage a JSON usando un ObjectMapper
        val objectMapper = ObjectMapper()
        objectMapper.writeValue(response.writer, errorMessage)
    }
}