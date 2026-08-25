package com.helpdesk.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: String,
    val name: String? = null,
    val email: String
)

@Serializable
data class TicketDto(
    val id: String,
    val subject: String,
    val description: String? = null,
    val html: String? = null,
    val status: String,
    val category: String,
    val source: String,
    val messageId: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val requester: CustomerDto,
    val assignee: UserDto? = null
)

@Serializable
data class PaginationDto(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int
)

@Serializable
data class TicketListResponse(
    val tickets: List<TicketDto>,
    val pagination: PaginationDto
)

@Serializable
data class TicketDetailResponse(
    val ticket: TicketDto
)

@Serializable
data class CreateTicketRequest(
    val subject: String,
    val description: String,
    val customerEmail: String
)

@Serializable
data class UpdateTicketRequest(
    val assigneeId: String? = null,
    val status: String? = null,
    val category: String? = null
)
