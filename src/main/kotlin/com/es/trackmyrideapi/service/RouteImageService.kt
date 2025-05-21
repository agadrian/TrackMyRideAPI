package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RouteImageRequest
import com.es.trackmyrideapi.dto.RouteImageResponse
import com.es.trackmyrideapi.model.RouteImage
import com.es.trackmyrideapi.repository.RouteImageRepository
import com.es.trackmyrideapi.repository.RouteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RouteImageService {

    @Autowired
    lateinit var routeRepository: RouteRepository

    @Autowired
    lateinit var routeImageRepository: RouteImageRepository

    fun addImageToRoute(routeId: Long, request: RouteImageRequest): RouteImageResponse {
        val route = routeRepository.findById(routeId)
            .orElseThrow { RuntimeException("Route not found") }

        val image = RouteImage(route = route, imageUrl = request.imageUrl)
        val saved = routeImageRepository.save(image)

        return RouteImageResponse(
            id = saved.id,
            imageUrl = saved.imageUrl,
            uploadedAt = saved.uploadedAt.toString()
        )
    }

    fun getImagesForRoute(routeId: Long): List<RouteImageResponse> {
        return routeImageRepository.findByRouteId(routeId).map {
            RouteImageResponse(
                id = it.id,
                imageUrl = it.imageUrl,
                uploadedAt = it.uploadedAt.toString()
            )
        }
    }

    fun deleteImage(routeId: Long, imageId: Long) {
        val image = routeImageRepository.findByIdAndRouteId(imageId, routeId)
            ?: throw RuntimeException("Image not found for this route")

        routeImageRepository.delete(image)
    }
}