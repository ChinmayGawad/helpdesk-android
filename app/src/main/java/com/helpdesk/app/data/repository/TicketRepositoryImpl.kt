package com.helpdesk.app.data.repository

import com.helpdesk.app.core.network.HelpdeskApiService
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.data.mapper.toDomain
import com.helpdesk.app.data.remote.dto.CreateReplyRequest
import com.helpdesk.app.data.remote.dto.CreateTicketRequest
import com.helpdesk.app.data.remote.dto.PolishReplyRequest
import com.helpdesk.app.data.remote.dto.UpdateTicketRequest
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketPage
import com.helpdesk.app.domain.model.TicketSortColumn
import com.helpdesk.app.domain.model.TicketSortDirection
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.repository.TicketRepository

class TicketRepositoryImpl(
    private val apiService: HelpdeskApiService
) : TicketRepository {

    override suspend fun getTickets(
        status: TicketStatus?,
        category: TicketCategory?,
        source: TicketSource?,
        searchQuery: String?,
        sortBy: TicketSortColumn,
        sortDir: TicketSortDirection,
        page: Int,
        perPage: Int
    ): Resource<TicketPage> {
        return safeApiCall(
            apiCall = {
                apiService.getTickets(
                    status = status?.value,
                    category = category?.value,
                    source = source?.value,
                    searchQuery = searchQuery,
                    sortBy = sortBy.value,
                    sortDir = sortDir.value,
                    page = page,
                    perPage = perPage
                )
            },
            transform = { it.toDomain() }
        )
    }

    override suspend fun getTicketDetail(id: String): Resource<Ticket> {
        return safeApiCall(
            apiCall = { apiService.getTicket(id) },
            transform = { it.ticket.toDomain() }
        )
    }

    override suspend fun createTicket(
        subject: String,
        description: String,
        customerEmail: String
    ): Resource<Ticket> {
        return safeApiCall(
            apiCall = {
                apiService.createTicket(
                    CreateTicketRequest(
                        subject = subject,
                        description = description,
                        customerEmail = customerEmail
                    )
                )
            },
            transform = { it.ticket.toDomain() }
        )
    }

    override suspend fun updateTicket(
        id: String,
        assigneeId: String?,
        status: TicketStatus?,
        category: TicketCategory?
    ): Resource<Ticket> {
        return safeApiCall(
            apiCall = {
                apiService.updateTicket(
                    id = id,
                    request = UpdateTicketRequest(
                        assigneeId = assigneeId,
                        status = status?.value,
                        category = category?.value
                    )
                )
            },
            transform = { it.ticket.toDomain() }
        )
    }

    override suspend fun getReplies(ticketId: String): Resource<List<Reply>> {
        return safeApiCall(
            apiCall = { apiService.getReplies(ticketId) },
            transform = { it.replies.map { replyDto -> replyDto.toDomain() } }
        )
    }

    override suspend fun createReply(ticketId: String, content: String): Resource<Reply> {
        return safeApiCall(
            apiCall = { apiService.createReply(ticketId, CreateReplyRequest(content)) },
            transform = { it.reply.toDomain() }
        )
    }

    override suspend fun polishReply(ticketId: String, content: String): Resource<String> {
        return safeApiCall(
            apiCall = { apiService.polishReply(ticketId, PolishReplyRequest(content)) },
            transform = { it.text }
        )
    }

    override suspend fun summarizeTicket(ticketId: String): Resource<String> {
        return safeApiCall(
            apiCall = { apiService.summarizeTicket(ticketId) },
            transform = { it.summary }
        )
    }

    override suspend fun getTicketStats(): Resource<TicketStats> {
        return safeApiCall(
            apiCall = { apiService.getStats() },
            transform = { it.toDomain() }
        )
    }

    override suspend fun getDailyStats(): Resource<List<DailyStat>> {
        return safeApiCall(
            apiCall = { apiService.getDailyStats() },
            transform = { list -> list.map { it.toDomain() } }
        )
    }
}
