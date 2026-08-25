package com.helpdesk.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatsResponse(
    val totalTickets: Int,
    val openTickets: Int,
    val aiResolvedTickets: Int,
    val aiResolvedPercentage: Double,
    val averageResolutionTime: String
)

@Serializable
data class DailyStatDto(
    val date: String,
    val count: Int
)

@Serializable
data class UserListResponse(
    val users: List<UserDto>
)

@Serializable
data class UserDetailResponse(
    val user: UserDto
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

@Serializable
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String
)

@Serializable
data class DeleteUserResponse(
    val success: Boolean? = true
)
