/**
 * Data layer: repository implementations, DTO mapping, and the safeApiCall helper
 * that wraps Retrofit calls in consistent error handling (401→Unauthorized, 403→Forbidden, etc.).
 */
package com.helpdesk.app.data.repository

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private val errorJson = Json { ignoreUnknownKeys = true }

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    transform: (T) -> R
): Resource<R> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(transform(body))
            } else {
                try {
                    @Suppress("UNCHECKED_CAST")
                    Resource.Success(Unit as R)
                } catch (_: Exception) {
                    Resource.Error(AppError.Server(response.code(), "Empty response body"))
                }
            }
        } else {
            val code = response.code()
            val rawError = response.errorBody()?.string()
            var errorMessage = "Request failed with status $code"

            if (!rawError.isNullOrBlank()) {
                try {
                    val jsonObj = errorJson.parseToJsonElement(rawError).jsonObject
                    val parsedError = jsonObj["error"]?.jsonPrimitive?.content
                        ?: jsonObj["message"]?.jsonPrimitive?.content
                    if (!parsedError.isNullOrBlank()) {
                        errorMessage = parsedError
                    }
                } catch (_: Exception) {
                    errorMessage = rawError
                }
            }

            when (code) {
                401 -> {
                    // 401 always means the session is no longer valid.
                    // Even when the server returns a custom body, treat it as
                    // Unauthorized so the app clears the session and routes the
                    // user back to login.
                    Resource.Error(AppError.Unauthorized)
                }
                403 -> Resource.Error(AppError.Forbidden(if (errorMessage.startsWith("Request failed")) "Permission denied. Admin role required." else errorMessage))
                429 -> Resource.Error(
                    AppError.Server(
                        429,
                        if (errorMessage.contains("too many", ignoreCase = true) || errorMessage.startsWith("Request failed")) {
                            "Rate limit reached (Too many requests). Please wait ~60 seconds and try again."
                        } else {
                            errorMessage
                        }
                    )
                )
                400, 409, 422 -> Resource.Error(AppError.Validation(errorMessage))
                else -> Resource.Error(AppError.Server(code, errorMessage))
            }
        }
    } catch (e: ConnectException) {
        Resource.Error(AppError.Network("Cannot connect to server. Ensure backend is running and device is connected to the same network (or USB port is reversed)."))
    } catch (e: SocketTimeoutException) {
        Resource.Error(AppError.Network("Connection timed out. Server took too long to respond."))
    } catch (e: UnknownHostException) {
        Resource.Error(AppError.Network("Cannot resolve server host address. Please check your Server settings."))
    } catch (e: IOException) {
        Resource.Error(AppError.Network(e.localizedMessage ?: "Network connection error. Please check your internet connection."))
    } catch (e: Exception) {
        Resource.Error(AppError.Unknown(e.localizedMessage ?: "An unexpected error occurred"))
    }
}
