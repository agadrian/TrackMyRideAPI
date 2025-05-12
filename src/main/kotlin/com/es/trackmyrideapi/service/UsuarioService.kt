package com.es.trackmyrideapi.service


import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    fun getAllUsuarios(): List<User> {
        return usuarioRepository.findAll()
    }

    fun getUsuarioById(id: String): User {
        return usuarioRepository.findById(id).orElseThrow { NotFoundException("Usuario con ID $id no encontrado") }
    }

    fun updateUsuario(id: String, usuario: User): User {
        val existingUsuario = getUsuarioById(id)
        existingUsuario.username = usuario.username
        existingUsuario.email = usuario.email
        existingUsuario.phone = usuario.phone
        existingUsuario.isPremium = usuario.isPremium
        return usuarioRepository.save(existingUsuario)
    }

    fun deleteUsuario(id: String) {
        val usuario = getUsuarioById(id)
        usuarioRepository.delete(usuario)
    }
}
