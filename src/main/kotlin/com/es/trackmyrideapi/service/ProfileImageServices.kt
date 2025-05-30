package com.es.trackmyrideapi.service

import com.cloudinary.Cloudinary
import com.es.trackmyrideapi.dto.ProfileImageRequest
import com.es.trackmyrideapi.dto.ProfileImageResponse
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.ProfileImage
import com.es.trackmyrideapi.repository.ProfileImageRepository
import com.es.trackmyrideapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class ProfileImageService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var profileImageRepository: ProfileImageRepository

    @Autowired
    private lateinit var cloudinary: Cloudinary


    fun updateProfileImage(userId: String, request: ProfileImageRequest): ProfileImageResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        val existingImage = profileImageRepository.findByUserUid(userId)

        if (existingImage != null) {
            // Borrar la imagen anterior de Cloudinary
            deleteFromCloudinary(existingImage.imageUrl)

            // Actualizar la URL y la fecha de subida
            existingImage.imageUrl = request.imageUrl
            existingImage.uploadedAt = LocalDateTime.now()

            val saved = profileImageRepository.save(existingImage)

            return ProfileImageResponse(
                id = saved.id,
                imageUrl = saved.imageUrl,
                uploadedAt = saved.uploadedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        } else {
            // No hay imagen previa, crear nueva
            val newImage = ProfileImage(
                user = user,
                imageUrl = request.imageUrl,
                uploadedAt = LocalDateTime.now()
            )
            val saved = profileImageRepository.save(newImage)

            return ProfileImageResponse(
                id = saved.id,
                imageUrl = saved.imageUrl,
                uploadedAt = saved.uploadedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
    }

    fun getProfileImage(userId: String): ProfileImageResponse {
        val image = profileImageRepository.findByUserUid(userId)
            ?: throw NotFoundException("No profile image found for this user")

        return ProfileImageResponse(
            id = image.id,
            imageUrl = image.imageUrl,
            uploadedAt = image.uploadedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    fun deleteProfileImage(userId: String) {
        val image = profileImageRepository.findByUserUid(userId)
            ?: throw NotFoundException("No profile image found to delete")

        deleteFromCloudinary(image.imageUrl)
        profileImageRepository.delete(image)
    }

    private fun deleteFromCloudinary(imageUrl: String) {
        val publicId = extractPublicIdFromUrl(imageUrl)

        try {
            val result = cloudinary.uploader().destroy(publicId, mapOf("invalidate" to true))
            if (result["result"] != "ok" && result["result"] != "not found") {
                throw RuntimeException("Failed to delete image from Cloudinary: $result")
            }
        } catch (e: Exception) {
            println("Error deleting image from Cloudinary: ${e.message}")
            throw RuntimeException("Error deleting image from Cloudinary: ${e.message}", e)
        }
    }

    private fun extractPublicIdFromUrl(url: String): String {
        val parts = url.split("/upload/")
        if (parts.size != 2) throw IllegalArgumentException("Invalid Cloudinary URL format")

        val afterUpload = parts[1]
        val publicIdWithExt = afterUpload.substringAfter("/")
        return publicIdWithExt.substringBeforeLast(".")
    }
}