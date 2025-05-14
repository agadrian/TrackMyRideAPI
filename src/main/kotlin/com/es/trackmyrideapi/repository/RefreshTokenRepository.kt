package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findByToken(token: String): RefreshToken?
    fun deleteByUserUid(userUid: String)
}