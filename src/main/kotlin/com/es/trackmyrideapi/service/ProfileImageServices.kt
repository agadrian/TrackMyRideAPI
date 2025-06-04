package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.ProfileImageRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.ProfileImage
import com.es.trackmyrideapi.repository.ProfileImageRepository
import com.es.trackmyrideapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class ProfileImageService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var profileImageRepository: ProfileImageRepository

    @Autowired
    private lateinit var authService: AuthService


    @Autowired
    private lateinit var cloudinaryService: CloudinaryService


    /**
     * Actualiza una imagen de perfil existente o crea una nueva para el usuario autenticado.
     * Si ya existe una imagen, se elimina de Cloudinary antes de actualizar.
     *
     * @param principal JWT del usuario autenticado.
     * @param request DTO que contiene la nueva URL de la imagen.
     * @return La entidad ProfileImage actualizada o creada.
     * @throws NotFoundException si el usuario no existe.
     */
    fun updateProfileImage(
        principal: Jwt,
        request: ProfileImageRequestDTO
    ): ProfileImage {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        val existingImage = profileImageRepository.findByUserUid(uid)

        return if (existingImage != null) {
            cloudinaryService.deleteFromCloudinary(existingImage.imageUrl)
            existingImage.imageUrl = request.imageUrl
            existingImage.uploadedAt = LocalDateTime.now()
            profileImageRepository.save(existingImage)
        } else {
            val newImage = ProfileImage(
                user = user,
                imageUrl = request.imageUrl,
                uploadedAt = LocalDateTime.now()
            )
            profileImageRepository.save(newImage)
        }
    }


    /**
     * Obtiene la imagen de perfil del usuario autenticado.
     *
     * @param principal JWT del usuario autenticado.
     * @return La entidad ProfileImage correspondiente al usuario.
     * @throws NotFoundException si el usuario o la imagen no existen.
     */
    fun getProfileImage(principal: Jwt): ProfileImage {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        return profileImageRepository.findByUserUid(uid)
            ?: throw NotFoundException("No profile image found for this user")
    }


    /**
     * Elimina la imagen de perfil del usuario autenticado y la borra de Cloudinary.
     *
     * @param principal JWT del usuario autenticado.
     * @throws NotFoundException si el usuario o la imagen no existen.
     */
    fun deleteProfileImage(principal: Jwt) {
        val uid = principal.getClaimAsString("uid")
        val user = userRepository.findByUid(uid)
            ?: throw NotFoundException("User not found")

        authService.checkUserIsSelfOrAdmin(principal, user.uid)

        val image = profileImageRepository.findByUserUid(uid)
            ?: throw NotFoundException("No profile image found to delete")

        cloudinaryService.deleteFromCloudinary(image.imageUrl)
        profileImageRepository.delete(image)
    }
}