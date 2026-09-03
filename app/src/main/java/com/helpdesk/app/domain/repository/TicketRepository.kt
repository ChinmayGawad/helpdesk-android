/**
 * Domain layer: business models, repository interfaces, and use-case classes.
 * This package is platform-agnostic and contains no Android dependencies.
 */
package com.helpdesk.app.domain.repository

import com.helpdesk.app.core.result.Resource
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

interface TicketRepository {
    suspend fun getTickets(
        status: TicketStatus? = null,
        category: TicketCategory? = null,
        source: TicketSource? = null,
        searchQuery: String? = null,
        sortBy: TicketSortColumn = TicketSortColumn.CREATED_AT,
        sortDir: TicketSortDirection = TicketSortDirection.DESC,
        page: Int = 1,
        perPage: Int = 20
    ): Resource<TicketPage>

    suspend fun getTicketDetail(id: String): Resource<Ticket>

    suspend fun createTicket(
        subject: String,
        description: String,
        customerEmail: String
    ): Resource<Ticket>

    suspend fun updateTicket(
        id: String,
        assigneeId: String? = null,
        status: TicketStatus? = null,
        category: TicketCategory? = null
    ): Resource<Ticket>

    suspend fun getReplies(ticketId: String): Resource<List<Reply>>

    suspend fun createReply(ticketId: String, content: String): Resource<Reply>

    suspend fun polishReply(ticketId: String, content: String): Resource<String>

    suspend fun summarizeTicket(ticketId: String): Resource<String>

    suspend fun getTicketStats(): Resource<TicketStats>

    suspend fun getDailyStats(): Resource<List<DailyStat>>
}
