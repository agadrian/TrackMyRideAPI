package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RefreshToken
import com.es.trackmyrideapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.token = :token")
    fun deleteByToken(@Param("token") token: String): Int
    fun findByToken(token: String): RefreshToken?
    fun findByUser(user: User): RefreshToken?
}