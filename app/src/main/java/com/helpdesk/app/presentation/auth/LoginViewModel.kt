package com.helpdesk.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.usecase.auth.GetBaseUrlUseCase
import com.helpdesk.app.domain.usecase.auth.GetCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.LoginUseCase
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.SetBaseUrlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "admin@helpdesk.local",
    val password: String = "admin12345",
    val baseUrl: String = "http://localhost:3000/",
    val isLoading: Boolean = false,
    val isCheckingSession: Boolean = true,
    val errorMessage: String? = null,
    val showServerSettings: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val setBaseUrlUseCase: SetBaseUrlUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = observeCurrentUserUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadInitialConfig()
        checkExistingSession()
    }

    private fun loadInitialConfig() {
        viewModelScope.launch {
            val url = getBaseUrlUseCase()
            _uiState.update { it.copy(baseUrl = url) }
        }
    }

    fun checkExistingSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingSession = true) }
            val result = getCurrentUserUseCase()
            _uiState.update { it.copy(isCheckingSession = false) }
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onBaseUrlChange(newBaseUrl: String) {
        _uiState.update { it.copy(baseUrl = newBaseUrl) }
    }

    fun saveBaseUrl(newBaseUrl: String) {
        viewModelScope.launch {
            setBaseUrlUseCase(newBaseUrl)
            _uiState.update { it.copy(baseUrl = newBaseUrl, showServerSettings = false) }
        }
    }

    fun toggleServerSettings(show: Boolean) {
        _uiState.update { it.copy(showServerSettings = show) }
    }

    fun login() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(currentState.email, currentState.password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
