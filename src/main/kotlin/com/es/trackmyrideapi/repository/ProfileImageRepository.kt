package com.es.trackmyrideapi.repository

import com.es.trackmyrideapi.model.ProfileImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileImageRepository : JpaRepository<ProfileImage, Long> {
    fun findByUserUid(uid: String): ProfileImage?
}