package com.helpdesk.app

import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.usecase.auth.LogoutUseCase
import com.helpdesk.app.domain.usecase.ticket.GetDailyStatsUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketStatsUseCase
import com.helpdesk.app.presentation.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeDashboardTicketRepository : com.helpdesk.app.domain.repository.TicketRepository {
    var stats: Resource<TicketStats> = Resource.Success(
        TicketStats(totalTickets = 10, openTickets = 4, aiResolvedTickets = 3, aiResolvedPercentage = 30.0, averageResolutionTime = "1h")
    )
    var dailyStats: Resource<List<DailyStat>> = Resource.Success(emptyList())

    override suspend fun getTickets(status: com.helpdesk.app.domain.model.TicketStatus?, category: com.helpdesk.app.domain.model.TicketCategory?, source: com.helpdesk.app.domain.model.TicketSource?, searchQuery: String?, sortBy: com.helpdesk.app.domain.model.TicketSortColumn, sortDir: com.helpdesk.app.domain.model.TicketSortDirection, page: Int, perPage: Int): Resource<com.helpdesk.app.domain.model.TicketPage> = Resource.Success(com.helpdesk.app.domain.model.TicketPage(emptyList(), com.helpdesk.app.domain.model.Pagination(1, 20, 0, 1)))
    override suspend fun getTicketDetail(id: String): Resource<com.helpdesk.app.domain.model.Ticket> = throw UnsupportedOperationException()
    override suspend fun createTicket(subject: String, description: String, customerEmail: String): Resource<com.helpdesk.app.domain.model.Ticket> = throw UnsupportedOperationException()
    override suspend fun updateTicket(id: String, assigneeId: String?, status: com.helpdesk.app.domain.model.TicketStatus?, category: com.helpdesk.app.domain.model.TicketCategory?): Resource<com.helpdesk.app.domain.model.Ticket> = throw UnsupportedOperationException()
    override suspend fun getReplies(ticketId: String): Resource<List<com.helpdesk.app.domain.model.Reply>> = throw UnsupportedOperationException()
    override suspend fun createReply(ticketId: String, content: String): Resource<com.helpdesk.app.domain.model.Reply> = throw UnsupportedOperationException()
    override suspend fun polishReply(ticketId: String, content: String): Resource<String> = throw UnsupportedOperationException()
    override suspend fun summarizeTicket(ticketId: String): Resource<String> = throw UnsupportedOperationException()
    override suspend fun getTicketStats(): Resource<TicketStats> = stats
    override suspend fun getDailyStats(): Resource<List<DailyStat>> = dailyStats
}

class FakeDashboardAuthRepository : com.helpdesk.app.domain.repository.AuthRepository {
    override suspend fun login(email: String, password: String): Resource<com.helpdesk.app.domain.model.User> = throw UnsupportedOperationException()
    override suspend fun logout(): Resource<Unit> = Resource.Success(Unit)
    override suspend fun getCurrentUser(): Resource<com.helpdesk.app.domain.model.User> = throw UnsupportedOperationException()
    override fun observeCurrentUser(): kotlinx.coroutines.flow.Flow<com.helpdesk.app.domain.model.User?> = kotlinx.coroutines.flow.flowOf(null)
    override suspend fun getBaseUrl(): String = ""
    override suspend fun setBaseUrl(url: String) {}
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DashboardViewModel
    private lateinit var fakeRepo: FakeDashboardTicketRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeDashboardTicketRepository()
        val fakeAuthRepo = FakeDashboardAuthRepository()
        viewModel = DashboardViewModel(
            GetTicketStatsUseCase(fakeRepo),
            GetDailyStatsUseCase(fakeRepo),
            LogoutUseCase(fakeAuthRepo)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty stats`() {
        val state = viewModel.uiState.value
        assertNull(state.stats)
    }

    @Test
    fun `loadDashboardData success updates stats`() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.stats)
        assertEquals(10, state.stats!!.totalTickets)
    }

    @Test
    fun `loadDashboardData sets errorMessage on failure`() = runTest(testDispatcher) {
        fakeRepo.stats = Resource.Error(AppError.Network("Network error"))
        viewModel.loadDashboardData()
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
    }

    private fun assertFalse(condition: Boolean) {
        org.junit.Assert.assertFalse(condition)
    }
}
