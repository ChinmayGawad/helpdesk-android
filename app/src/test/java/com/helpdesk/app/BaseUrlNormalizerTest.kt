package com.helpdesk.app

import com.helpdesk.app.core.util.BaseUrlNormalizer
import org.junit.Assert.assertEquals
import org.junit.Test

class BaseUrlNormalizerTest {

    @Test
    fun `normalize adds https to plain domain`() {
        val result = BaseUrlNormalizer.normalize("helpdesk.example.com")
        assertEquals("https://helpdesk.example.com/", result)
    }

    @Test
    fun `normalize adds http to localhost`() {
        val result = BaseUrlNormalizer.normalize("localhost:8080")
        assertEquals("http://localhost:8080/", result)
    }

    @Test
    fun `normalize adds http to loopback address`() {
        val result = BaseUrlNormalizer.normalize("127.0.0.1:3000")
        assertEquals("http://127.0.0.1:3000/", result)
    }

    @Test
    fun `normalize adds http to emulator host`() {
        val result = BaseUrlNormalizer.normalize("10.0.2.2:8080")
        assertEquals("http://10.0.2.2:8080/", result)
    }

    @Test
    fun `normalize adds http to LAN address`() {
        val result = BaseUrlNormalizer.normalize("192.168.1.100")
        assertEquals("http://192.168.1.100/", result)
    }

    @Test
    fun `normalize preserves existing scheme`() {
        val result = BaseUrlNormalizer.normalize("https://api.example.com")
        assertEquals("https://api.example.com/", result)
    }

    @Test
    fun `normalize preserves http scheme for localhost`() {
        val result = BaseUrlNormalizer.normalize("http://localhost")
        assertEquals("http://localhost/", result)
    }

    @Test
    fun `normalize adds trailing slash`() {
        val result = BaseUrlNormalizer.normalize("https://example.com")
        assertEquals("https://example.com/", result)
    }

    @Test
    fun `normalize keeps existing trailing slash`() {
        val result = BaseUrlNormalizer.normalize("https://example.com/")
        assertEquals("https://example.com/", result)
    }

    @Test
    fun `normalize trims whitespace`() {
        val result = BaseUrlNormalizer.normalize("  https://example.com  ")
        assertEquals("https://example.com/", result)
    }
}
