package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.RouteImageRequestDTO
import com.es.trackmyrideapi.dto.RouteImageResponseDTO
import com.es.trackmyrideapi.mappers.toResponseDTO
import com.es.trackmyrideapi.service.RouteImageService
import com.es.trackmyrideapi.service.RouteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/routes")
class RouteImageController {

    @Autowired
    private lateinit var routeImageService: RouteImageService


    /**
     * Sube una imagen a la ruta especificada.
     *
     * @param routeId ID de la ruta a la que se agregará la imagen.
     * @param request DTO con la información de la imagen a subir.
     * @param principal Información de autenticación del usuario actual.
     * @return Respuesta HTTP con el DTO de la imagen creada.
     */
    @PostMapping("/{routeId}/images")
    fun uploadImage(
        @PathVariable routeId: Long,
        @RequestBody request: RouteImageRequestDTO,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteImageResponseDTO> {
        val image = routeImageService.addImageToRoute(routeId, request, principal)
        return ResponseEntity.ok(image.toResponseDTO())
    }


    /**
     * Obtiene todas las imágenes asociadas a una ruta.
     *
     * @param routeId ID de la ruta de la cual se desean obtener las imágenes.
     * @param principal Información de autenticación del usuario actual.
     * @return Respuesta HTTP con la lista de DTOs de imágenes.
     */
    @GetMapping("/{routeId}/images")
    fun getImages(
        @PathVariable routeId: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<List<RouteImageResponseDTO>> {
        val images = routeImageService.getImagesForRoute(routeId, principal)
        return ResponseEntity.ok(images.map { it.toResponseDTO() })
    }


    /**
     * Elimina una imagen específica de una ruta.
     *
     * @param routeId ID de la ruta a la que pertenece la imagen.
     * @param imageId ID de la imagen a eliminar.
     * @param principal Información de autenticación del usuario actual.
     * @return Respuesta HTTP sin contenido.
     */
    @DeleteMapping("/{routeId}/images/{imageId}")
    fun deleteImage(
        @PathVariable routeId: Long,
        @PathVariable imageId: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        routeImageService.deleteImage(routeId, imageId, principal)
        return ResponseEntity.noContent().build()
    }
}