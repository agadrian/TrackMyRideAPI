package com.es.trackmyrideapi.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.security.authentication.InternalAuthenticationServiceException


// Controller advice permite que el manejo de errores sea centralizado, y no tenga que especificar el manejo en cada controlador

// Cada ExceptrionHandler maneja un tipo de excepcion especifica y devuelve un ResponseEntity<ErrorMessage>, incluyendo el codigo HTTP y el mensaje de error

// En lugar de usar @ResponseStatus, devolvemos expplicitamente un ResponseEntity con el codigoa decuado, por ej, HttpStatus.BAD_REQUEST o HttpStatus.NOT_FOUND, y el objeto ErrorMessage que contiene el mensaje de error y la ruta de la solicitud (request.requestURI)

// Hacerlo de esta forma permite que se contorle los errores en APIExceptionHandler, por tanto el flujo de la app no se corta, como ocurre al usar ResponseStatus y no 'pete' la aplicacion.

@ControllerAdvice
class APIExceptionHandler {

    // Manejo de BadRequestException (400)
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(request: HttpServletRequest, exception: BadRequestException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.BAD_REQUEST.value(),
            message = exception.message ?: "Bad request",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    // Manejo de NotFoundException (404)
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(request: HttpServletRequest, exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.NOT_FOUND.value(),
            message = exception.message ?: "Not found",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }



    // Manejo de AlreadyExists (404)
    @ExceptionHandler(ConflictException::class, AlreadyExistsException::class)
    fun alreadyExists(request: HttpServletRequest, exception: AlreadyExistsException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.CONFLICT.value(),
            message = exception.message ?: "Error ocurred",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.CONFLICT)
    }


    // Manejo de UnauthorizedException (401)
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(request: HttpServletRequest, exception: UnauthorizedException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = exception.message ?: "Unauthorized access",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.UNAUTHORIZED)
    }

    // Manejo de ForbiddenException (403)
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(request: HttpServletRequest, exception: ForbiddenException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.FORBIDDEN.value(),
            message = exception.message ?: "Access is forbidden",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.FORBIDDEN)
    }


    // Manejo de InternalAuthenticationServiceException (401) (login incorrecto)
    @ExceptionHandler(InternalAuthenticationServiceException::class)
    fun handleAuthenticationException(request: HttpServletRequest, exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = "Login no efectuado, credenciales incorrectas",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.UNAUTHORIZED)
    }


    // Manejo de InternalAuthenticationServiceException (401) (login incorrecto)
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(request: HttpServletRequest, e: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = "Login no efectuado, credenciales incorrectas",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.UNAUTHORIZED)
    }

    // Manejo de cualquier otra excepción no controlada (500)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(request: HttpServletRequest, exception: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = exception.message ?: "An unexpected error occurred",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @ExceptionHandler(GeneralAppException::class)
    fun handleGeneralAppException(request: HttpServletRequest, exception: GeneralAppException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = exception.message ?: "Unexpected server error",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @ExceptionHandler(FirebaseException::class)
    fun handleFirebaseException(request: HttpServletRequest, exception: FirebaseException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = exception.message ?: "Error while interacting with Firebase",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}