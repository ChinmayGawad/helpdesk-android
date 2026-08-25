package com.helpdesk.app.domain.usecase.auth

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !EMAIL_REGEX.matches(trimmedEmail)) {
            return Resource.Error(AppError.Validation("Please enter a valid email address"))
        }
        if (password.length < 8) {
            return Resource.Error(AppError.Validation("Password must be at least 8 characters"))
        }
        return authRepository.login(trimmedEmail, password)
    }
}

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Resource<Unit> = authRepository.logout()
}

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Resource<User> = authRepository.getCurrentUser()
}

class ObserveCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
}

class GetBaseUrlUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): String = authRepository.getBaseUrl()
}

class SetBaseUrlUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(url: String) = authRepository.setBaseUrl(url)
}
