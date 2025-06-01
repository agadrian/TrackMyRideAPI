package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.token = :token")
    fun deleteByToken(@Param("token") token: String): Int
    fun findByToken(token: String): RefreshToken?
    fun deleteAllByUserUid(userUid: String)
    fun findByUserUid(userUid: String): RefreshToken?
}