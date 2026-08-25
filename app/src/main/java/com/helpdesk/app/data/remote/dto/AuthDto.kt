package com.helpdesk.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String? = "agent",
    val emailVerified: Boolean? = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SessionDto(
    val id: String? = null,
    val userId: String? = null,
    val token: String? = null,
    val expiresAt: String? = null
)

@Serializable
data class AuthResponse(
    val user: UserDto? = null,
    val session: SessionDto? = null,
    val token: String? = null,
    val error: String? = null,
    val message: String? = null
)

@Serializable
data class MeResponse(
    val user: UserDto,
    val session: SessionDto? = null
)
