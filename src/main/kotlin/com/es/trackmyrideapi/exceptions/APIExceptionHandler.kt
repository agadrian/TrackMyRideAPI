package com.es.trackmyrideapi.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.security.authentication.InternalAuthenticationServiceException


/**
 * Manejador global de excepciones para la API REST.
 *
 * Esta clase, anotada con `@ControllerAdvice`, captura y gestiona de forma centralizada
 * las excepciones que se lanzan en los controladores de la aplicación.
 *
 * Cada método marcado con `@ExceptionHandler` maneja un tipo específico de excepción,
 * devolviendo un objeto `ResponseEntity<ErrorMessage>` con:
 *  - Código HTTP apropiado
 *  - Mensaje descriptivo del error
 *  - Ruta de la solicitud que causó la excepción
 *
 * Esto evita duplicar la lógica de manejo de errores en cada controlador, permite
 * respuestas de error consistentes y mantiene el flujo de la aplicación sin interrumpirse abruptamente.
 *
 * Excepciones gestionadas incluyen:
 *  - BadRequestException (400)
 *  - NotFoundException (404)
 *  - ConflictException, AlreadyExistsException (409)
 *  - UnauthorizedException (401)
 *  - ForbiddenException (403)
 *  - InternalAuthenticationServiceException, BadCredentialsException (401)
 *  - GeneralAppException, FirebaseException (500)
 *  - Otras excepciones no controladas (500)
 *
 * @see ErrorMessage Clase que encapsula la información del mensaje de error.
 */
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
            message = "Incorrect credentias",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.UNAUTHORIZED)
    }


    // Manejo de InternalAuthenticationServiceException (401) (login incorrecto)
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(request: HttpServletRequest, e: Exception): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = "Incorrect credentias",
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

    @ExceptionHandler(CloudinaryException::class)
    fun handleCloudinaryException(request: HttpServletRequest, exception: CloudinaryException): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = exception.message ?: "Cloudinary error",
            path = request.requestURI
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}