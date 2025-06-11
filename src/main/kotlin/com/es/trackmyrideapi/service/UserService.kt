package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.exceptions.FirebaseException
import com.es.trackmyrideapi.exceptions.GeneralAppException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.ProfileImage
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service


@Service
class UserService {

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var authService: AuthService

    /**
     * Obtiene el listado de todos los usuarios.
     *
     * @return Lista de todos los usuarios.
     */
    fun getAllUsuarios(): List<User> {
        return userRepository.findAllByRoleNot("ADMIN")
    }


    /**
     * Obtiene los datos de un usuario por su ID. Solo el propio usuario o un ADMIN puede acceder.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param id ID del usuario a obtener.
     * @return Usuario encontrado.
     * @throws NotFoundException Si el usuario no existe.
     * @throws ForbiddenException Si el usuario autenticado no tiene permisos.
     */
    fun getUsuarioById(principal: Jwt, id: String): User {
        authService.checkUserIsSelfOrAdmin(principal, id)

        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        return user
    }

    /**
     * Actualiza los datos del usuario. Solo el propio usuario o un ADMIN puede modificarlo.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param id ID del usuario a actualizar.
     * @param usuarioDTO Datos nuevos del usuario.
     * @return Usuario actualizado.
     * @throws NotFoundException Si el usuario no existe.
     * @throws ForbiddenException Si el usuario autenticado no tiene permisos.
     */
    fun updateUsuario(
        principal: Jwt,
        id: String,
        usuarioDTO: UserUpdateDTO
    ): User {
        authService.checkUserIsSelfOrAdmin(principal, id)

        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        val updatedUser = user.copy(
            username = usuarioDTO.username ?: user.username,
            phone = usuarioDTO.phone ?: user.phone,
            //photoUrl = updateDto.photoUrl ?: user.photoUrl
        )

        return userRepository.save(updatedUser)
    }


    /**
     * Elimina un usuario tanto en Firebase como en la base de datos local.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param id ID del usuario a eliminar.
     * @throws NotFoundException Si el usuario no existe.
     * @throws ForbiddenException Si el usuario autenticado no tiene permisos.
     * @throws FirebaseException Si ocurre un error al eliminar en Firebase.
     * @throws GeneralAppException Si ocurre un error al eliminar en la base de datos.
     */
    @Transactional
    fun deleteUsuario(principal: Jwt, id: String) {
        authService.checkUserIsSelfOrAdmin(principal, id)

        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        // Intentar borrar en firebase
        try {
            FirebaseAuth.getInstance().deleteUser(user.uid)
        } catch (e: Exception) {
            throw FirebaseException("Error deleting user in Firebase: ${e.message}")
        }

        try {
            // Si Firebase ok, borrar de la base de datos
            deleteUsuarioFromDatabase(principal, user.uid)
        } catch (e: Exception) {
            try {
                // Restaurar usuario en Firebase (si tienes los datos)
                val request = UserRecord.CreateRequest()
                    .setUid(user.uid)
                    .setEmail(user.email)
                    .setDisplayName(user.username)
                FirebaseAuth.getInstance().createUser(request)
            } catch (e: Exception) {
                throw FirebaseException("Error deleting user in Firebase: ${e.message}")
            }
            throw GeneralAppException("Error deleting user from databases: ${e.message}")
        }
    }

    /**
     * Elimina el usuario en la base de datos.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param id ID del usuario.
     * @throws NotFoundException Si el usuario no existe.
     * @throws ForbiddenException Si no tiene permisos.
     */
    @Transactional
    fun deleteUsuarioFromDatabase(principal: Jwt, id: String) {
        authService.checkUserIsSelfOrAdmin(principal, id)

        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        userRepository.deleteById(user.uid)
    }


    /**
     * Verifica si el usuario actual es premium.
     *
     * @param principal Token JWT del usuario autenticado.
     * @return true si el usuario es premium, false si no.
     * @throws NotFoundException Si el usuario no existe.
     */
    fun isUserPremium(principal: Jwt): Boolean {
        val userId = principal.getClaimAsString("uid")
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User id $userId not found") }

        return user.isPremium
    }


    /**
     * Cambia el estado premium de un usuario.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param isPremium Nuevo valor para la suscripción premium.
     * @param id ID del usuario a actualizar.
     * @return Usuario actualizado.
     * @throws NotFoundException Si el usuario no existe.
     * @throws ForbiddenException Si el usuario no tiene permisos.
     */
    fun setUserPremium(
        principal: Jwt,
        isPremium: Boolean,
        id: String
    ): User {
        authService.checkUserIsSelfOrAdmin(principal, id)

        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        val updatedUser = user.copy(isPremium = isPremium)
        val savedUser = userRepository.save(updatedUser)

        return savedUser
    }


    /**
     * Actualiza la imagen de perfil del usuario autenticado.
     *
     * @param principal Token JWT del usuario autenticado.
     * @param imageUrl URL de la nueva imagen.
     * @return Usuario actualizado con la nueva imagen.
     * @throws NotFoundException Si el usuario no existe.
     */
    fun updateProfileImage(
        principal: Jwt,
        imageUrl: String
    ): User {
        val userId = principal.getClaimAsString("uid")
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User id $userId not found") }

        val newImage = ProfileImage(imageUrl = imageUrl, user = user)
        user.profileImage = newImage

        userRepository.save(user)
        return user
    }
}
