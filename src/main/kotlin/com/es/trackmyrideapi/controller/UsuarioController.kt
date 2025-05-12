package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.model.User
import com.es.trackmyrideapi.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    /**
     * Obtener listado de todos los usuarios
     */
    @GetMapping("/")
    fun getAllUsuarios(): ResponseEntity<List<User>> {
        val usuarios = usuarioService.getAllUsuarios()
        return ResponseEntity(usuarios, HttpStatus.OK)
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    fun getUsuarioById(@PathVariable id: String): ResponseEntity<User> {
        val usuario = usuarioService.getUsuarioById(id)
        return ResponseEntity(usuario, HttpStatus.OK)
    }

    /**
     * Actualizar usuario por ID
     */
    @PutMapping("/{id}")
    fun updateUsuario(@PathVariable id: String, @RequestBody usuario: User): ResponseEntity<User> {
        val updatedUsuario = usuarioService.updateUsuario(id, usuario)
        return ResponseEntity(updatedUsuario, HttpStatus.OK)
    }

    /**
     * Eliminar usuario por ID
     */
    @DeleteMapping("/{id}")
    fun deleteUsuario(@PathVariable id: String) {
        usuarioService.deleteUsuario(id)
    }
}