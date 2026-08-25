package com.helpdesk.app.domain.repository

import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun logout(): Resource<Unit>
    suspend fun getCurrentUser(): Resource<User>
    fun observeCurrentUser(): Flow<User?>
    suspend fun getBaseUrl(): String
    suspend fun setBaseUrl(url: String)
}
