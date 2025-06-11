package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, String> {
    fun findByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun existsByUid(uid: String): Boolean
    fun findByUid(uid: String): User?
    fun findAllByRoleNot(role: String): List<User>
    fun existsByUsername(username: String): Boolean
}