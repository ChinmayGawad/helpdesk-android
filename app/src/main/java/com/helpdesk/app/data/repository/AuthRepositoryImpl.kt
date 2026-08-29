package com.helpdesk.app.data.repository

import com.helpdesk.app.core.datastore.SessionManager
import com.helpdesk.app.core.network.HelpdeskApiService
import com.helpdesk.app.core.network.SessionCookieJar
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.data.mapper.toDomain
import com.helpdesk.app.data.remote.dto.SignInRequest
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val apiService: HelpdeskApiService,
    private val sessionManager: SessionManager,
    private val sessionCookieJar: SessionCookieJar
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        var token: String? = null
        val result = safeApiCall(
            apiCall = { apiService.signIn(SignInRequest(email, password)) },
            transform = { response ->
                token = response.token ?: response.session?.token
                val userDto = response.user
                if (userDto != null) {
                    userDto.toDomain()
                } else {
                    throw IllegalStateException("No user returned in auth response")
                }
            }
        )

        if (result is Resource.Success) {
            sessionManager.saveSession(result.data, token)
        }
        return result
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            apiService.signOut()
            sessionCookieJar.clear()
            sessionManager.clearSession()
            Resource.Success(Unit)
        } catch (e: Exception) {
            sessionCookieJar.clear()
            sessionManager.clearSession()
            Resource.Success(Unit)
        }
    }

    override suspend fun getCurrentUser(): Resource<User> {
        val result = safeApiCall(
            apiCall = { apiService.getMe() },
            transform = { it.user.toDomain() }
        )
        if (result is Resource.Success) {
            val existingToken = sessionManager.getSessionToken()
            sessionManager.saveSession(result.data, existingToken)
        }
        return result
    }

    override fun observeCurrentUser(): Flow<User?> = sessionManager.userFlow

    override suspend fun getBaseUrl(): String = sessionManager.getBaseUrl()

    override suspend fun setBaseUrl(url: String) = sessionManager.setBaseUrl(url)
}
