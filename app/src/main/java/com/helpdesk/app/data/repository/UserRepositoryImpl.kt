package com.helpdesk.app.data.repository

import com.helpdesk.app.core.network.HelpdeskApiService
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.data.mapper.toDomain
import com.helpdesk.app.data.remote.dto.CreateUserRequest
import com.helpdesk.app.data.remote.dto.UpdateUserRequest
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: HelpdeskApiService
) : UserRepository {

    override suspend fun getUsers(): Resource<List<User>> {
        return safeApiCall(
            apiCall = { apiService.getUsers() },
            transform = { it.users.map { userDto -> userDto.toDomain() } }
        )
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Resource<User> {
        return safeApiCall(
            apiCall = {
                apiService.createUser(
                    CreateUserRequest(
                        name = name,
                        email = email,
                        password = password,
                        role = role.value
                    )
                )
            },
            transform = { it.user.toDomain() }
        )
    }

    override suspend fun updateUser(
        id: String,
        name: String,
        email: String,
        password: String?,
        role: UserRole
    ): Resource<User> {
        return safeApiCall(
            apiCall = {
                apiService.updateUser(
                    id = id,
                    request = UpdateUserRequest(
                        name = name,
                        email = email,
                        password = password,
                        role = role.value
                    )
                )
            },
            transform = { it.user.toDomain() }
        )
    }

    override suspend fun deleteUser(id: String): Resource<Unit> {
        return safeApiCall(
            apiCall = { apiService.deleteUser(id) },
            transform = { Unit }
        )
    }
}
