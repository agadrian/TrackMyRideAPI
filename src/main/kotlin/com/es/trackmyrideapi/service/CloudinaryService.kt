package com.es.trackmyrideapi.service

import com.cloudinary.Cloudinary
import com.es.trackmyrideapi.exceptions.CloudinaryException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CloudinaryService {

    @Autowired
    lateinit var cloudinary: Cloudinary

    /**
     * Extrae el publicId requerido para eliminar una imagen de Cloudinary a partir de su URL.
     *
     * @param url URL completa de la imagen en Cloudinary.
     * @return El publicId que se usa para operaciones con Cloudinary.
     * @throws IllegalArgumentException Si el formato de la URL no es válido.
     */
    fun extractPublicIdFromUrl(url: String): String {
        val parts = url.split("/upload/")
        if (parts.size != 2) throw IllegalArgumentException("Invalid Cloudinary URL format")

        val afterUpload = parts[1]
        val publicIdWithExt = afterUpload.substringAfter("/")
        return publicIdWithExt.substringBeforeLast(".")
    }

    fun deleteFromCloudinary(imageUrl: String) {
        val publicId = extractPublicIdFromUrl(imageUrl)

        try {
            val result = cloudinary.uploader().destroy(publicId, mapOf("invalidate" to true))
            if (result["result"] != "ok" && result["result"] != "not found") {
                throw CloudinaryException("Failed to delete image from Cloudinary: $result")
            }
        } catch (e: Exception) {
            println("Error deleting image from Cloudinary: ${e.message}")
            throw CloudinaryException("Error deleting image from Cloudinary: ${e.message}")
        }
    }
}