package com.es.trackmyrideapi.service


import com.es.trackmyrideapi.dto.UserUpdateDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun getAllUsuarios(): List<User> {
        return userRepository.findAll()
    }

    fun getUsuarioById(id: String): User {
        return userRepository.findById(id).orElseThrow { NotFoundException("User id $id not found") }
    }

    fun updateUsuario(id: String, usuarioDTO: UserUpdateDTO): User {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User id $id not found") }

        val updatedUser = user.copy(
            username = usuarioDTO.username ?: user.username,
            phone = usuarioDTO.phone ?: user.phone,
            //photoUrl = updateDto.photoUrl ?: user.photoUrl
        )

        return userRepository.save(updatedUser)
    }



    fun deleteUsuario(id: String) {
        if (!userRepository.existsById(id)) {
            throw NotFoundException("User id $id not found")
        }
        userRepository.deleteById(id)
    }
}
