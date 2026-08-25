package com.helpdesk.app.data.mapper

import com.helpdesk.app.data.remote.dto.CustomerDto
import com.helpdesk.app.data.remote.dto.DailyStatDto
import com.helpdesk.app.data.remote.dto.PaginationDto
import com.helpdesk.app.data.remote.dto.ReplyDto
import com.helpdesk.app.data.remote.dto.StatsResponse
import com.helpdesk.app.data.remote.dto.TicketDto
import com.helpdesk.app.data.remote.dto.TicketListResponse
import com.helpdesk.app.data.remote.dto.UserDto
import com.helpdesk.app.domain.model.Customer
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.Pagination
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.ReplySenderType
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketPage
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = UserRole.fromValue(role),
    createdAt = createdAt
)

fun CustomerDto.toDomain(): Customer = Customer(
    id = id,
    name = name,
    email = email
)

fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    subject = subject,
    description = description ?: "",
    html = html,
    status = TicketStatus.fromValue(status),
    category = TicketCategory.fromValue(category),
    source = TicketSource.fromValue(source),
    messageId = messageId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    requester = requester.toDomain(),
    assignee = assignee?.toDomain()
)

fun PaginationDto.toDomain(): Pagination = Pagination(
    page = page,
    perPage = perPage,
    total = total,
    totalPages = totalPages
)

fun TicketListResponse.toDomain(): TicketPage = TicketPage(
    tickets = tickets.map { it.toDomain() },
    pagination = pagination.toDomain()
)

fun ReplyDto.toDomain(): Reply = Reply(
    id = id,
    content = content,
    ticketId = ticketId,
    authorId = authorId,
    senderType = ReplySenderType.fromValue(senderType),
    author = author.toDomain(),
    createdAt = createdAt
)

fun StatsResponse.toDomain(): TicketStats = TicketStats(
    totalTickets = totalTickets,
    openTickets = openTickets,
    aiResolvedTickets = aiResolvedTickets,
    aiResolvedPercentage = aiResolvedPercentage,
    averageResolutionTime = averageResolutionTime
)

fun DailyStatDto.toDomain(): DailyStat = DailyStat(
    date = date,
    count = count
)
