package com.es.trackmyrideapi.service


import com.es.trackmyrideapi.dto.UserResponseDTO
import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.dto.toResponseDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

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



    fun deleteUsuario(id: String) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException("User id $id not found")
        }
        userRepository.deleteById(id)
    }
}
