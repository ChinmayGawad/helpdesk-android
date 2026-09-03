package com.helpdesk.app.presentation.tickets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.usecase.ticket.CreateReplyUseCase
import com.helpdesk.app.domain.usecase.ticket.GetRepliesUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketDetailUseCase
import com.helpdesk.app.domain.usecase.ticket.PolishReplyUseCase
import com.helpdesk.app.domain.usecase.ticket.SummarizeTicketUseCase
import com.helpdesk.app.domain.usecase.ticket.UpdateTicketUseCase
import com.helpdesk.app.domain.usecase.user.GetUsersUseCase
import timber.log.Timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val replies: List<Reply> = emptyList(),
    val availableAgents: List<User> = emptyList(),
    val isLoadingTicket: Boolean = false,
    val isLoadingReplies: Boolean = false,
    val isUpdatingTicket: Boolean = false,
    val isSendingReply: Boolean = false,
    val isPolishingReply: Boolean = false,
    val isSummarizing: Boolean = false,
    val aiSummary: String? = null,
    val draftReply: String = "",
    val polishedReply: String? = null,
    val showPolishPreviewDialog: Boolean = false,
    val showStatusSheet: Boolean = false,
    val showAssigneeSheet: Boolean = false,
    val errorMessage: String? = null,
    val replyErrorMessage: String? = null
)

class TicketDetailViewModel(
    private val ticketId: String,
    private val getTicketDetailUseCase: GetTicketDetailUseCase,
    private val getRepliesUseCase: GetRepliesUseCase,
    private val createReplyUseCase: CreateReplyUseCase,
    private val polishReplyUseCase: PolishReplyUseCase,
    private val summarizeTicketUseCase: SummarizeTicketUseCase,
    private val updateTicketUseCase: UpdateTicketUseCase,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    init {
        loadTicketData()
        loadReplies()
        loadAgents()
    }

    fun loadTicketData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTicket = true, errorMessage = null) }
            val result = getTicketDetailUseCase(ticketId)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(ticket = result.data, isLoadingTicket = false) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingTicket = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun loadReplies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReplies = true) }
            val result = getRepliesUseCase(ticketId)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(replies = result.data, isLoadingReplies = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingReplies = false) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadAgents() {
        viewModelScope.launch {
            val result = getUsersUseCase()
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(availableAgents = result.data) }
                }
                is Resource.Error -> {
                    Timber.w("Failed to load agents: ${result.error.toUserMessage()}")
                    _uiState.update { it.copy(errorMessage = "Failed to load agents: ${result.error.toUserMessage()}") }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onDraftReplyChange(newDraft: String) {
        _uiState.update { it.copy(draftReply = newDraft, replyErrorMessage = null) }
    }

    fun onDraftChange(newDraft: String) = onDraftReplyChange(newDraft)

    fun updateStatus(status: TicketStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingTicket = true, showStatusSheet = false) }
            val result = updateTicketUseCase(id = ticketId, status = status)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(ticket = result.data, isUpdatingTicket = false) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingTicket = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateCategory(category: TicketCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingTicket = true) }
            val result = updateTicketUseCase(id = ticketId, category = category)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(ticket = result.data, isUpdatingTicket = false) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingTicket = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateAssignee(assigneeId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingTicket = true, showAssigneeSheet = false) }
            val result = updateTicketUseCase(id = ticketId, assigneeId = assigneeId)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(ticket = result.data, isUpdatingTicket = false) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingTicket = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun generateAISummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSummarizing = true) }
            val result = summarizeTicketUseCase(ticketId)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(aiSummary = result.data, isSummarizing = false) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSummarizing = false,
                            errorMessage = "AI Summary failed: ${result.error.toUserMessage()}"
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun polishDraftReply() {
        val draft = _uiState.value.draftReply.trim()
        if (draft.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPolishingReply = true, replyErrorMessage = null) }
            val result = polishReplyUseCase(ticketId, draft)
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            polishedReply = result.data,
                            isPolishingReply = false,
                            showPolishPreviewDialog = true
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isPolishingReply = false,
                            replyErrorMessage = "AI Polish failed: ${result.error.toUserMessage()}"
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun applyPolishedReply() {
        val polished = _uiState.value.polishedReply
        if (!polished.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    draftReply = polished,
                    polishedReply = null,
                    showPolishPreviewDialog = false
                )
            }
        }
    }

    fun dismissPolishPreview() {
        _uiState.update { it.copy(showPolishPreviewDialog = false) }
    }

    fun sendReply() {
        val content = _uiState.value.draftReply.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingReply = true, replyErrorMessage = null) }
            val result = createReplyUseCase(ticketId, content)
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            draftReply = "",
                            isSendingReply = false,
                            replies = it.replies + result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSendingReply = false,
                            replyErrorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleStatusSheet(show: Boolean) {
        _uiState.update { it.copy(showStatusSheet = show) }
    }

    fun toggleAssigneeSheet(show: Boolean) {
        _uiState.update { it.copy(showAssigneeSheet = show) }
    }
}
