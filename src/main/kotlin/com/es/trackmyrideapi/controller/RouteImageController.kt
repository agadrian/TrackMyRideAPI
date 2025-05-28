package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.RouteImageRequest
import com.es.trackmyrideapi.dto.RouteImageResponse
import com.es.trackmyrideapi.exceptions.ForbiddenException
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

    @Autowired
    private lateinit var routeService: RouteService

    @PostMapping("/{routeId}/images")
    fun uploadImage(
        @PathVariable routeId: Long,
        @RequestBody request: RouteImageRequest,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<RouteImageResponse> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val route = routeService.getRouteById(routeId)

        // Verificamos que el usuario sea el dueño o ADMIN
        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to upload images to this route")
        }

        val response = routeImageService.addImageToRoute(routeId, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{routeId}/images")
    fun getImages(
        @PathVariable routeId: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<List<RouteImageResponse>> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val route = routeService.getRouteById(routeId)

        // Verificamos que el usuario sea el dueño o ADMIN
        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to view images from this route")
        }

        return ResponseEntity.ok(routeImageService.getImagesForRoute(routeId))
    }

    @DeleteMapping("/{routeId}/images/{imageId}")
    fun deleteImage(
        @PathVariable routeId: Long,
        @PathVariable imageId: Long,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        val userId = principal.getClaimAsString("uid")
        val role = principal.getClaimAsString("role")

        val route = routeService.getRouteById(routeId)

        if (route.userId != userId && role != "ADMIN") {
            throw ForbiddenException("You don't have permission to delete images from this route")
        }

        routeImageService.deleteImage(routeId, imageId)

        return ResponseEntity.noContent().build()
    }
}