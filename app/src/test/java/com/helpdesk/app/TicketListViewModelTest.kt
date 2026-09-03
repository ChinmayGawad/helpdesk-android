package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.Customer
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.Pagination
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.ReplySenderType
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketPage
import com.helpdesk.app.domain.model.TicketSortColumn
import com.helpdesk.app.domain.model.TicketSortDirection
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.repository.TicketRepository
import com.helpdesk.app.domain.usecase.ticket.CreateTicketUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketsUseCase
import com.helpdesk.app.presentation.tickets.list.TicketListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeTicketRepositoryForList : TicketRepository {
    var tickets = listOf(
        Ticket(
            id = "t1", subject = "Login issue", description = "Can't login",
            status = TicketStatus.OPEN, category = TicketCategory.GENERAL_QUESTION,
            source = TicketSource.WEB, createdAt = "2026-08-29T10:00:00Z",
            requester = Customer("c1", "Jane", "jane@example.com")
        )
    )
    var page = 1
    var shouldFail = false

    override suspend fun getTickets(
        status: TicketStatus?, category: TicketCategory?, source: TicketSource?,
        searchQuery: String?, sortBy: TicketSortColumn, sortDir: TicketSortDirection,
        page: Int, perPage: Int
    ): Resource<TicketPage> {
        if (shouldFail) return Resource.Error(AppError.Network("Fail"))
        this.page = page
        return Resource.Success(
            TicketPage(
                tickets = tickets,
                pagination = Pagination(page = page, perPage = perPage, total = tickets.size, totalPages = 1)
            )
        )
    }

    override suspend fun getTicketDetail(id: String): Resource<Ticket> = throw UnsupportedOperationException()
    override suspend fun createTicket(subject: String, description: String, customerEmail: String): Resource<Ticket> {
        val ticket = Ticket(id = "new_t", subject = subject, description = description,
            status = TicketStatus.NEW, category = TicketCategory.GENERAL_QUESTION,
            source = TicketSource.WEB, createdAt = "2026-08-29T10:00:00Z",
            requester = Customer("c1", "Customer", customerEmail))
        return Resource.Success(ticket)
    }
    override suspend fun updateTicket(id: String, assigneeId: String?, status: TicketStatus?, category: TicketCategory?): Resource<Ticket> = throw UnsupportedOperationException()
    override suspend fun getReplies(ticketId: String): Resource<List<Reply>> = throw UnsupportedOperationException()
    override suspend fun createReply(ticketId: String, content: String): Resource<Reply> = throw UnsupportedOperationException()
    override suspend fun polishReply(ticketId: String, content: String): Resource<String> = throw UnsupportedOperationException()
    override suspend fun summarizeTicket(ticketId: String): Resource<String> = throw UnsupportedOperationException()
    override suspend fun getTicketStats(): Resource<TicketStats> = throw UnsupportedOperationException()
    override suspend fun getDailyStats(): Resource<List<DailyStat>> = throw UnsupportedOperationException()
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TicketListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TicketListViewModel
    private val fakeRepo = FakeTicketRepositoryForList()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TicketListViewModel(GetTicketsUseCase(fakeRepo), CreateTicketUseCase(fakeRepo))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty tickets`() {
        val state = viewModel.uiState.value
        assertTrue(state.tickets.isEmpty())
    }

    @Test
    fun `loadTickets success populates tickets`() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.tickets.size)
        assertEquals("t1", state.tickets[0].id)
    }

    @Test
    fun `loadTickets failure sets errorMessage`() = runTest(testDispatcher) {
        fakeRepo.shouldFail = true
        viewModel.loadTickets()
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `search debounce triggers new load after delay`() = runTest(testDispatcher) {
        viewModel.onSearchQueryChange("login")
        testScheduler.advanceTimeBy(400)
        val state = viewModel.uiState.value
        assertEquals("login", state.searchQuery)
    }

    @Test
    fun `filter by status updates selectedStatus`() {
        viewModel.onStatusFilterSelect(TicketStatus.OPEN)
        val state = viewModel.uiState.value
        assertEquals(TicketStatus.OPEN, state.selectedStatus)
    }

    @Test
    fun `createTicket success`() = runTest(testDispatcher) {
        var capturedTicket: Ticket? = null
        viewModel.createTicket("Help", "Need help", "user@example.com") { capturedTicket = it }
        testScheduler.advanceUntilIdle()
        assertNotNull(capturedTicket)
        assertEquals("Help", capturedTicket?.subject)
    }

    @Test
    fun `createTicket with invalid email fails validation`() = runTest(testDispatcher) {
        viewModel.createTicket("Help", "Need help", "bad-email") { }
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.createTicketError?.contains("valid") == true)
    }
}
