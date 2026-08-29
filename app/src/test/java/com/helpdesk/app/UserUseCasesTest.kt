package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.UserRepository
import com.helpdesk.app.domain.usecase.user.CreateUserUseCase
import com.helpdesk.app.domain.usecase.user.DeleteUserUseCase
import com.helpdesk.app.domain.usecase.user.GetUsersUseCase
import com.helpdesk.app.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeUserRepository : UserRepository {
    private val users = mutableListOf(
        User(id = "u1", name = "Admin", email = "admin@helpdesk.local", role = UserRole.ADMIN)
    )

    override suspend fun getUsers(): Resource<List<User>> = Resource.Success(users.toList())

    override suspend fun createUser(name: String, email: String, password: String, role: UserRole): Resource<User> {
        val created = User(id = "u_${users.size + 1}", name = name, email = email, role = role)
        users.add(created)
        return Resource.Success(created)
    }

    override suspend fun updateUser(id: String, name: String, email: String, password: String?, role: UserRole): Resource<User> {
        val index = users.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = users[index]
            val updated = current.copy(
                name = name,
                email = email,
                role = role
            )
            users[index] = updated
            return Resource.Success(updated)
        }
        return Resource.Error(AppError.Server(404, "User not found"))
    }

    override suspend fun deleteUser(id: String): Resource<Unit> {
        users.removeAll { it.id == id }
        return Resource.Success(Unit)
    }
}

class UserUseCasesTest {

    private val fakeRepo = FakeUserRepository()
    private val createUserUseCase = CreateUserUseCase(fakeRepo)
    private val updateUserUseCase = UpdateUserUseCase(fakeRepo)
    private val deleteUserUseCase = DeleteUserUseCase(fakeRepo)
    private val getUsersUseCase = GetUsersUseCase(fakeRepo)

    @Test
    fun createUser_invalidEmail_failsValidation() = runTest {
        val result = createUserUseCase("John", "invalid", "password123", UserRole.AGENT)
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun createUser_shortPassword_failsValidation() = runTest {
        val result = createUserUseCase("John", "john@example.com", "short", UserRole.AGENT)
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun createUser_validInput_succeeds() = runTest {
        val result = createUserUseCase("John Doe", "john@example.com", "password123", UserRole.AGENT)
        assertTrue(result is Resource.Success<*>)
        assertEquals("John Doe", (result as Resource.Success<User>).data.name)
    }

    @Test
    fun deleteUser_succeeds() = runTest {
        val result = deleteUserUseCase("u1")
        assertTrue(result is Resource.Success<*>)
        val usersResult = getUsersUseCase()
        assertTrue((usersResult as Resource.Success<List<User>>).data.isEmpty())
    }
}
