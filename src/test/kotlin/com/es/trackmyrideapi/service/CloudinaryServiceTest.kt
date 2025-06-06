package com.es.trackmyrideapi.service

import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CloudinaryServiceTest {

    private lateinit var service: CloudinaryService
    private val cloudinary: Cloudinary = mockk()
    private val uploader: Uploader = mockk()

    @BeforeEach
    fun setUp() {
        service = CloudinaryService()
        service.cloudinary = cloudinary

        every { cloudinary.uploader() } returns uploader
    }

    @Test
    fun `extractPublicIdFromUrl should extract publicId correctly`() {
        val url = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg"

        val publicId = service.extractPublicIdFromUrl(url)

        // El publicId esperado es "v123456/sample"
        assertEquals("sample", publicId)
    }

    @Test
    fun `extractPublicIdFromUrl should throw exception for invalid url`() {
        val invalidUrl = "https://res.cloudinary.com/demo/image/otherpath/sample.jpg"

        assertThrows<IllegalArgumentException> {
            service.extractPublicIdFromUrl(invalidUrl)
        }
    }

    @Test
    fun `deleteFromCloudinary should call destroy with correct publicId and succeed`() {
        val url = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg"
        val publicId = "sample"

        every { uploader.destroy(publicId, mapOf("invalidate" to true)) } returns mapOf("result" to "ok")

        service.deleteFromCloudinary(url)

        verify(exactly = 1) { uploader.destroy(publicId, mapOf("invalidate" to true)) }
    }

    @Test
    fun `deleteFromCloudinary should throw RuntimeException if destroy returns failure`() {
        val url = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg"
        val publicId = "sample"

        every { uploader.destroy(publicId, mapOf("invalidate" to true)) } returns mapOf("result" to "error")

        val ex = assertThrows<RuntimeException> {
            service.deleteFromCloudinary(url)
        }
        assert(ex.message!!.contains("Failed to delete image"))
    }

    @Test
    fun `deleteFromCloudinary should catch and rethrow exceptions`() {
        val url = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg"
        val publicId = "sample"

        every { uploader.destroy(publicId, mapOf("invalidate" to true)) } throws RuntimeException("Cloudinary down")

        val ex = assertThrows<RuntimeException> {
            service.deleteFromCloudinary(url)
        }
        assert(ex.message!!.contains("Error deleting image"))
    }
}
