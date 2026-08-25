package com.helpdesk.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.usecase.auth.LogoutUseCase
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.domain.usecase.ticket.GetDailyStatsUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val stats: TicketStats? = null,
    val dailyStats: List<DailyStat> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val getTicketStatsUseCase: GetTicketStatsUseCase,
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val logoutUseCase: LogoutUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val statsResult = getTicketStatsUseCase()
            val dailyResult = getDailyStatsUseCase()

            var error: String? = null
            var stats: TicketStats? = _uiState.value.stats
            var daily: List<DailyStat> = _uiState.value.dailyStats

            when (statsResult) {
                is Resource.Success -> stats = statsResult.data
                is Resource.Error -> error = statsResult.error.toUserMessage()
                is Resource.Loading -> Unit
            }

            when (dailyResult) {
                is Resource.Success -> daily = dailyResult.data
                is Resource.Error -> if (error == null) error = dailyResult.error.toUserMessage()
                is Resource.Loading -> Unit
            }

            _uiState.update {
                it.copy(
                    stats = stats,
                    dailyStats = daily,
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = error
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }
}
