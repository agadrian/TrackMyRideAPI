package com.es.trackmyrideapi.service

import com.cloudinary.Cloudinary
import com.es.trackmyrideapi.dto.RouteImageRequest
import com.es.trackmyrideapi.dto.RouteImageResponse
import com.es.trackmyrideapi.exceptions.NotFoundException
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

    @Autowired
    lateinit var cloudinary: Cloudinary

    fun addImageToRoute(routeId: Long, request: RouteImageRequest): RouteImageResponse {
        val route = routeRepository.findById(routeId)
            .orElseThrow { NotFoundException("Route not found") }

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
            ?: throw NotFoundException("Image not found for this route")

         // Eliminar de Cloudinary
        val publicId = extractPublicIdFromUrl(image.imageUrl)

        try {
            val result = cloudinary.uploader().destroy(publicId, mapOf("invalidate" to true))
            if (result["result"] != "ok" && result["result"] != "not found") {
                // Si la eliminación falla o no es "ok", lanzar excepción para rollback
                throw RuntimeException("Failed to delete image from Cloudinary: $result")
            }
        } catch (e: Exception) {
            println("Error deleting image from Cloudinary: ${e.message}")
            throw RuntimeException("Error deleting image from Cloudinary: ${e.message}", e)
        }

        // Si llega aqui esta ok, eliminar de la BD
        routeImageRepository.delete(image)
    }

    private fun extractPublicIdFromUrl(url: String): String {
        val parts = url.split("/upload/")
        if (parts.size != 2) throw IllegalArgumentException("Invalid Cloudinary URL format")

        val afterUpload = parts[1] //"v1748428889/tc28kowkojhnbbtg7bs2.jpg"

        // El public_id después de la barra ("/") que sigue a la versión
        val publicIdWithExt = afterUpload.substringAfter("/") // "tc28kowkojhnbbtg7bs2.jpg"

        return publicIdWithExt.substringBeforeLast(".") // "tc28kowkojhnbbtg7bs2"
    }
}