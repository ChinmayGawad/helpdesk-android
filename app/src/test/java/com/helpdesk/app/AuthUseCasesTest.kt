package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.AuthRepository
import com.helpdesk.app.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeAuthRepository : AuthRepository {
    var loggedInUser: User? = null
    var shouldFail = false

    override suspend fun login(email: String, password: String): Resource<User> {
        if (shouldFail) return Resource.Error(AppError.Unauthorized)
        val user = User(id = "1", name = "Admin", email = email, role = UserRole.ADMIN)
        loggedInUser = user
        return Resource.Success(user)
    }

    override suspend fun logout(): Resource<Unit> {
        loggedInUser = null
        return Resource.Success(Unit)
    }

    override suspend fun getCurrentUser(): Resource<User> {
        return loggedInUser?.let { Resource.Success(it) } ?: Resource.Error(AppError.Unauthorized)
    }

    override fun observeCurrentUser(): Flow<User?> = flowOf(loggedInUser)

    override suspend fun getBaseUrl(): String = "https://example.com/"

    override suspend fun setBaseUrl(url: String) {}
}

class AuthUseCasesTest {

    private val fakeRepo = FakeAuthRepository()
    private val loginUseCase = LoginUseCase(fakeRepo)

    @Test
    fun login_invalidEmail_returnsValidationError() = runTest {
        val result = loginUseCase("invalid-email", "password123")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
        assertEquals("Please enter a valid email address", (result.error as AppError.Validation).message)
    }

    @Test
    fun login_shortPassword_returnsValidationError() = runTest {
        val result = loginUseCase("admin@helpdesk.local", "short")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
        assertEquals("Password must be at least 8 characters", (result.error as AppError.Validation).message)
    }

    @Test
    fun login_validCredentials_succeeds() = runTest {
        val result = loginUseCase("admin@helpdesk.local", "admin12345")
        assertTrue(result is Resource.Success)
        assertEquals("admin@helpdesk.local", (result as Resource.Success).data.email)
    }
}
