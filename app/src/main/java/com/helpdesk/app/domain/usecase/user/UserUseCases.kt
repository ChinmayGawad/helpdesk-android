package com.helpdesk.app.domain.usecase.user

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.UserRepository

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class GetUsersUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Resource<List<User>> = userRepository.getUsers()
}

class CreateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Resource<User> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.length < 3) {
            return Resource.Error(AppError.Validation("Name must be at least 3 characters"))
        }
        if (trimmedEmail.isBlank() || !EMAIL_REGEX.matches(trimmedEmail)) {
            return Resource.Error(AppError.Validation("Please enter a valid email address"))
        }
        if (password.length < 8) {
            return Resource.Error(AppError.Validation("Password must be at least 8 characters"))
        }

        return userRepository.createUser(trimmedName, trimmedEmail, password, role)
    }
}

class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        id: String,
        name: String,
        email: String,
        password: String?,
        role: UserRole
    ): Resource<User> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (id.isBlank()) return Resource.Error(AppError.Validation("User ID cannot be empty"))
        if (trimmedName.length < 3) {
            return Resource.Error(AppError.Validation("Name must be at least 3 characters"))
        }
        if (trimmedEmail.isBlank() || !EMAIL_REGEX.matches(trimmedEmail)) {
            return Resource.Error(AppError.Validation("Please enter a valid email address"))
        }
        if (!password.isNullOrEmpty() && password.length < 8) {
            return Resource.Error(AppError.Validation("Password must be at least 8 characters"))
        }

        return userRepository.updateUser(
            id = id,
            name = trimmedName,
            email = trimmedEmail,
            password = if (password.isNullOrBlank()) null else password,
            role = role
        )
    }
}

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        if (id.isBlank()) return Resource.Error(AppError.Validation("User ID cannot be empty"))
        return userRepository.deleteUser(id)
    }
}
