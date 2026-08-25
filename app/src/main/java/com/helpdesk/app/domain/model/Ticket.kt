package com.helpdesk.app.domain.model

data class Ticket(
    val id: String,
    val subject: String,
    val description: String = "",
    val html: String? = null,
    val status: TicketStatus,
    val category: TicketCategory,
    val source: TicketSource,
    val messageId: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val requester: Customer,
    val assignee: User? = null
)

data class Pagination(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int
)

data class TicketPage(
    val tickets: List<Ticket>,
    val pagination: Pagination
)
