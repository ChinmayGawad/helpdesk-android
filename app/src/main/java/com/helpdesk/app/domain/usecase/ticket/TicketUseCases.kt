package com.helpdesk.app.domain.usecase.ticket

import com.helpdesk.app.core.result.AppError
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
import com.helpdesk.app.domain.repository.TicketRepository

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class GetTicketsUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(
        status: TicketStatus? = null,
        category: TicketCategory? = null,
        source: TicketSource? = null,
        searchQuery: String? = null,
        sortBy: TicketSortColumn = TicketSortColumn.CREATED_AT,
        sortDir: TicketSortDirection = TicketSortDirection.DESC,
        page: Int = 1,
        perPage: Int = 20
    ): Resource<TicketPage> {
        return ticketRepository.getTickets(
            status = status,
            category = category,
            source = source,
            searchQuery = searchQuery?.trim()?.ifBlank { null },
            sortBy = sortBy,
            sortDir = sortDir,
            page = page,
            perPage = perPage
        )
    }
}

class GetTicketDetailUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(id: String): Resource<Ticket> {
        if (id.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        return ticketRepository.getTicketDetail(id)
    }
}

class CreateTicketUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(
        subject: String,
        description: String,
        customerEmail: String
    ): Resource<Ticket> {
        val trimmedSubject = subject.trim()
        val trimmedDesc = description.trim()
        val trimmedEmail = customerEmail.trim()

        if (trimmedSubject.isBlank()) {
            return Resource.Error(AppError.Validation("Subject is required"))
        }
        if (trimmedDesc.isBlank()) {
            return Resource.Error(AppError.Validation("Description is required"))
        }
        if (trimmedEmail.isBlank() || !EMAIL_REGEX.matches(trimmedEmail)) {
            return Resource.Error(AppError.Validation("Please enter a valid customer email"))
        }

        return ticketRepository.createTicket(trimmedSubject, trimmedDesc, trimmedEmail)
    }
}

class UpdateTicketUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(
        id: String,
        assigneeId: String? = null,
        status: TicketStatus? = null,
        category: TicketCategory? = null
    ): Resource<Ticket> {
        if (id.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        return ticketRepository.updateTicket(
            id = id,
            assigneeId = assigneeId,
            status = status,
            category = category
        )
    }
}

class GetRepliesUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(ticketId: String): Resource<List<Reply>> {
        if (ticketId.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        return ticketRepository.getReplies(ticketId)
    }
}

class CreateReplyUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(ticketId: String, content: String): Resource<Reply> {
        val trimmed = content.trim()
        if (ticketId.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        if (trimmed.isBlank()) return Resource.Error(AppError.Validation("Reply cannot be empty"))
        if (trimmed.length > 10000) return Resource.Error(AppError.Validation("Reply is too long (max 10,000 characters)"))
        return ticketRepository.createReply(ticketId, trimmed)
    }
}

class PolishReplyUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(ticketId: String, content: String): Resource<String> {
        val trimmed = content.trim()
        if (ticketId.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        if (trimmed.isBlank()) return Resource.Error(AppError.Validation("Cannot polish an empty draft"))
        if (trimmed.length > 10000) return Resource.Error(AppError.Validation("Draft is too long (max 10,000 characters)"))
        return ticketRepository.polishReply(ticketId, trimmed)
    }
}

class SummarizeTicketUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(ticketId: String): Resource<String> {
        if (ticketId.isBlank()) return Resource.Error(AppError.Validation("Ticket ID cannot be empty"))
        return ticketRepository.summarizeTicket(ticketId)
    }
}

class GetTicketStatsUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(): Resource<TicketStats> = ticketRepository.getTicketStats()
}

class GetDailyStatsUseCase(private val ticketRepository: TicketRepository) {
    suspend operator fun invoke(): Resource<List<DailyStat>> = ticketRepository.getDailyStats()
}
