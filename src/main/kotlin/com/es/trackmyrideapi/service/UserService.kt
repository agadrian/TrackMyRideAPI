package com.es.trackmyrideapi.service


import com.es.trackmyrideapi.dto.UserResponseDTO
import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.dto.toResponseDTO
import com.es.trackmyrideapi.exceptions.FirebaseException
import com.es.trackmyrideapi.exceptions.GeneralAppException
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import com.es.trackmyrideapi.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    fun getAllUsuarios(): List<UserResponseDTO> {
        return userRepository.findAll().map { it.toResponseDTO() }
    }

    fun getUsuarioById(id: String): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }
        return user.toResponseDTO()
    }

    fun updateUsuario(id: String, usuarioDTO: UserUpdateDTO): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        val updatedUser = user.copy(
            username = usuarioDTO.username ?: user.username,
            phone = usuarioDTO.phone ?: user.phone,
            //photoUrl = updateDto.photoUrl ?: user.photoUrl
        )

        val savedUser = userRepository.save(updatedUser)

        return savedUser.toResponseDTO()
    }


    @Transactional
    fun deleteUsuario(id: String) {
        val user = userRepository.findById(id).orElseThrow({ NotFoundException("User id $id not found") })

        // Intentar borrar en firebase
        try {
            FirebaseAuth.getInstance().deleteUser(user.uid)
        } catch (e: Exception) {
            throw FirebaseException("Error deleting user in Firebase: ${e.message}")
        }

        try {
            // Si Firebase ok, borrar de la base de datos
            deleteUsuarioFromDatabase(user.uid)
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

    @Transactional
    fun deleteUsuarioFromDatabase(uid: String) {
        userRepository.deleteById(uid)
    }

    fun isUserPremium(id: String): Boolean {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }
        return user.isPremium
    }

    fun setUserPremium(id: String, isPremium: Boolean): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        val updatedUser = user.copy(isPremium = isPremium)
        val savedUser = userRepository.save(updatedUser)

        return savedUser.toResponseDTO()
    }
}
