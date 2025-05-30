package com.es.trackmyrideapi.controller

import com.es.trackmyrideapi.dto.ProfileImageRequest
import com.es.trackmyrideapi.dto.ProfileImageResponse
import com.es.trackmyrideapi.service.ProfileImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/profile-image")
class ProfileImageController {

    @Autowired
    private lateinit var profileImageService: ProfileImageService

    @PutMapping
    fun updateProfileImage(
        @RequestBody request: ProfileImageRequest,
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<ProfileImageResponse> {
        val userId = principal.getClaimAsString("uid")
        val response = profileImageService.updateProfileImage(userId, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getProfileImage(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<ProfileImageResponse> {
        val userId = principal.getClaimAsString("uid")
        val response = profileImageService.getProfileImage(userId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping
    fun deleteProfileImage(
        @AuthenticationPrincipal principal: Jwt
    ): ResponseEntity<Unit> {
        val userId = principal.getClaimAsString("uid")
        profileImageService.deleteProfileImage(userId)
        return ResponseEntity.noContent().build()
    }
}