package com.helpdesk.app.domain.repository

import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole

interface UserRepository {
    suspend fun getUsers(): Resource<List<User>>
    suspend fun createUser(name: String, email: String, password: String, role: UserRole): Resource<User>
    suspend fun updateUser(id: String, name: String, email: String, password: String?, role: UserRole): Resource<User>
    suspend fun deleteUser(id: String): Resource<Unit>
}
