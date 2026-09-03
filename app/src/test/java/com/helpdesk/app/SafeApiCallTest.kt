package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.data.repository.safeApiCall
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SafeApiCallTest {

    @Test
    fun `successful response returns Success with transformed data`() = runTest {
        val response = Response.success(200, """{"name":"test"}""".toResponseBody("application/json".toMediaType()))
        val result = safeApiCall(
            apiCall = { response },
            transform = { "mapped" }
        )
        assertTrue(result is Resource.Success)
        assertEquals("mapped", (result as Resource.Success<String>).data)
    }

    @Test
    fun `null body on success returns Unit`() = runTest {
        val response = Response.success<Unit>(200, null)
        val result = safeApiCall(
            apiCall = { response },
            transform = { Unit }
        )
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `401 response maps to Unauthorized`() = runTest {
        val body = """{"error":"session expired"}""".toResponseBody("application/json".toMediaType())
        val response = Response.error<Unit>(401, body)
        val result = safeApiCall(
            apiCall = { response },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Unauthorized)
    }

    @Test
    fun `403 response maps to Forbidden`() = runTest {
        val body = """{"error":"permission denied"}""".toResponseBody("application/json".toMediaType())
        val response = Response.error<Unit>(403, body)
        val result = safeApiCall(
            apiCall = { response },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Forbidden)
    }

    @Test
    fun `400 response maps to Validation`() = runTest {
        val body = """{"error":"bad request"}""".toResponseBody("application/json".toMediaType())
        val response = Response.error<Unit>(400, body)
        val result = safeApiCall(
            apiCall = { response },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun `500 response maps to Server error`() = runTest {
        val body = """{"message":"internal error"}""".toResponseBody("application/json".toMediaType())
        val response = Response.error<Unit>(500, body)
        val result = safeApiCall(
            apiCall = { response },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Server)
    }

    @Test
    fun `connection exception maps to Network error`() = runTest {
        val result = safeApiCall<Unit, Unit>(
            apiCall = { throw java.net.ConnectException("Connection refused") },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Network)
    }

    @Test
    fun `unknown host exception maps to Network error`() = runTest {
        val result = safeApiCall<Unit, Unit>(
            apiCall = { throw java.net.UnknownHostException("No such host") },
            transform = { Unit }
        )
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Network)
    }
}
