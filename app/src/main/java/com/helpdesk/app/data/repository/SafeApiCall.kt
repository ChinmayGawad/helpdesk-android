package com.helpdesk.app.data.repository

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import java.io.IOException

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
                401 -> Resource.Error(AppError.Unauthorized)
                400, 409, 422 -> Resource.Error(AppError.Validation(errorMessage))
                else -> Resource.Error(AppError.Server(code, errorMessage))
            }
        }
    } catch (e: IOException) {
        Resource.Error(AppError.Network(e.localizedMessage ?: "Network connection error"))
    } catch (e: Exception) {
        Resource.Error(AppError.Unknown(e.localizedMessage ?: "An unexpected error occurred"))
    }
}
