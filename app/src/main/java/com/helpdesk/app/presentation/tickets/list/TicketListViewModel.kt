package com.helpdesk.app.presentation.tickets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.Pagination
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketSortColumn
import com.helpdesk.app.domain.model.TicketSortDirection
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.usecase.ticket.CreateTicketUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketListUiState(
    val tickets: List<Ticket> = emptyList(),
    val pagination: Pagination? = null,
    val selectedStatus: TicketStatus? = null,
    val selectedCategory: TicketCategory? = null,
    val selectedSource: TicketSource? = null,
    val searchQuery: String = "",
    val sortBy: TicketSortColumn = TicketSortColumn.CREATED_AT,
    val sortDir: TicketSortDirection = TicketSortDirection.DESC,
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isCreatingTicket: Boolean = false,
    val createTicketError: String? = null,
    val showFilterSheet: Boolean = false,
    val showCreateDialog: Boolean = false
)

class TicketListViewModel(
    private val getTicketsUseCase: GetTicketsUseCase,
    private val createTicketUseCase: CreateTicketUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketListUiState())
    val uiState: StateFlow<TicketListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var requestJob: Job? = null

    init {
        loadTickets()
    }

    fun loadTickets(isRefresh: Boolean = false, page: Int = 1) {
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val state = _uiState.value
            val result = getTicketsUseCase(
                status = state.selectedStatus,
                category = state.selectedCategory,
                source = state.selectedSource,
                searchQuery = state.searchQuery.ifBlank { null },
                sortBy = state.sortBy,
                sortDir = state.sortDir,
                page = page,
                perPage = 20
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            tickets = result.data.tickets,
                            pagination = result.data.pagination,
                            currentPage = page,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            loadTickets(page = 1)
        }
    }

    fun onStatusFilterSelect(status: TicketStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadTickets(page = 1)
    }

    fun onCategoryFilterSelect(category: TicketCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadTickets(page = 1)
    }

    fun onSourceFilterSelect(source: TicketSource?) {
        _uiState.update { it.copy(selectedSource = source) }
        loadTickets(page = 1)
    }

    fun onSortChange(sortBy: TicketSortColumn, sortDir: TicketSortDirection) {
        _uiState.update { it.copy(sortBy = sortBy, sortDir = sortDir, showFilterSheet = false) }
        loadTickets(page = 1)
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.update { it.copy(showFilterSheet = show) }
    }

    fun toggleCreateDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateDialog = show, createTicketError = null) }
    }

    fun createTicket(
        subject: String,
        description: String,
        customerEmail: String,
        onSuccess: (Ticket) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingTicket = true, createTicketError = null) }
            val result = createTicketUseCase(subject, description, customerEmail)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isCreatingTicket = false, showCreateDialog = false) }
                    loadTickets(page = 1)
                    onSuccess(result.data)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isCreatingTicket = false,
                            createTicketError = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
