package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.Customer
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.ReplySenderType
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketPage
import com.helpdesk.app.domain.model.TicketSortColumn
import com.helpdesk.app.domain.model.TicketSortDirection
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.TicketRepository
import com.helpdesk.app.domain.usecase.ticket.CreateReplyUseCase
import com.helpdesk.app.domain.usecase.ticket.CreateTicketUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

import com.helpdesk.app.domain.model.Pagination

class FakeTicketRepository : TicketRepository {
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
        return Resource.Success(
            TicketPage(
                tickets = emptyList(),
                pagination = Pagination(page = 1, perPage = 20, total = 0, totalPages = 1)
            )
        )
    }

    override suspend fun getTicketDetail(id: String): Resource<Ticket> {
        val ticket = Ticket(
            id = id,
            subject = "Test",
            description = "Description",
            status = TicketStatus.OPEN,
            category = TicketCategory.GENERAL_QUESTION,
            source = TicketSource.WEB,
            createdAt = "2026-08-29T10:00:00Z",
            requester = Customer(id = "c1", name = "Test", email = "test@example.com")
        )
        return Resource.Success(ticket)
    }

    override suspend fun createTicket(subject: String, description: String, customerEmail: String): Resource<Ticket> {
        val ticket = Ticket(
            id = "t_new",
            subject = subject,
            description = description,
            status = TicketStatus.NEW,
            category = TicketCategory.GENERAL_QUESTION,
            source = TicketSource.WEB,
            createdAt = "2026-08-29T10:00:00Z",
            requester = Customer(id = "c1", name = "Customer", email = customerEmail)
        )
        return Resource.Success(ticket)
    }

    override suspend fun updateTicket(
        id: String,
        assigneeId: String?,
        status: TicketStatus?,
        category: TicketCategory?
    ): Resource<Ticket> {
        return getTicketDetail(id)
    }

    override suspend fun getReplies(ticketId: String): Resource<List<Reply>> = Resource.Success(emptyList())

    override suspend fun createReply(ticketId: String, content: String): Resource<Reply> {
        return Resource.Success(
            Reply(
                id = "r_new",
                ticketId = ticketId,
                authorId = "u1",
                author = User(id = "u1", name = "Admin", email = "admin@helpdesk.local", role = UserRole.ADMIN),
                senderType = ReplySenderType.AGENT,
                content = content,
                createdAt = "2026-08-29T10:00:00Z"
            )
        )
    }

    override suspend fun polishReply(ticketId: String, content: String): Resource<String> = Resource.Success(content)

    override suspend fun summarizeTicket(ticketId: String): Resource<String> = Resource.Success("Summary")

    override suspend fun getTicketStats(): Resource<TicketStats> = Resource.Success(TicketStats(10, 4, 3, 30.0, "1h"))

    override suspend fun getDailyStats(): Resource<List<DailyStat>> = Resource.Success(emptyList())
}

class TicketUseCasesTest {

    private val fakeRepo = FakeTicketRepository()
    private val createTicketUseCase = CreateTicketUseCase(fakeRepo)
    private val createReplyUseCase = CreateReplyUseCase(fakeRepo)

    @Test
    fun createTicket_invalidEmail_failsValidation() = runTest {
        val result = createTicketUseCase("Help", "Need help", "invalid_email")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun createTicket_emptySubject_failsValidation() = runTest {
        val result = createTicketUseCase("   ", "Need help", "user@example.com")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun createTicket_validPayload_succeeds() = runTest {
        val result = createTicketUseCase("Login Issue", "Cannot reset password", "user@example.com")
        assertTrue(result is Resource.Success<*>)
        assertEquals("Login Issue", (result as Resource.Success<Ticket>).data.subject)
    }

    @Test
    fun createReply_emptyContent_failsValidation() = runTest {
        val result = createReplyUseCase("t1", "   ")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).error is AppError.Validation)
    }

    @Test
    fun createReply_validContent_succeeds() = runTest {
        val result = createReplyUseCase("t1", "Here is the solution.")
        assertTrue(result is Resource.Success<*>)
        assertEquals("Here is the solution.", (result as Resource.Success<Reply>).data.content)
    }
}
