package com.helpdesk.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.datastore.SessionManager
import com.helpdesk.app.core.result.AppError
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.usecase.auth.GetBaseUrlUseCase
import com.helpdesk.app.domain.usecase.auth.GetCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.LoginUseCase
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.SetBaseUrlUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

sealed interface ConnectionTestStatus {
    data object Idle : ConnectionTestStatus
    data object Testing : ConnectionTestStatus
    data class Success(val responseTimeMs: Long, val statusCode: Int) : ConnectionTestStatus
    data class Failed(val error: String) : ConnectionTestStatus
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val baseUrl: String = SessionManager.DEFAULT_BASE_URL,
    val isLoading: Boolean = false,
    val isCheckingSession: Boolean = true,
    val errorMessage: String? = null,
    val showServerSettings: Boolean = false,
    val connectionStatus: ConnectionTestStatus = ConnectionTestStatus.Idle
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

    private val probeClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

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
            getCurrentUserUseCase()
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
        _uiState.update { it.copy(baseUrl = newBaseUrl, connectionStatus = ConnectionTestStatus.Idle) }
    }

    fun saveBaseUrl(newBaseUrl: String) {
        viewModelScope.launch {
            setBaseUrlUseCase(newBaseUrl)
            _uiState.update { it.copy(baseUrl = newBaseUrl, showServerSettings = false, connectionStatus = ConnectionTestStatus.Idle) }
        }
    }

    fun toggleServerSettings(show: Boolean) {
        _uiState.update { it.copy(showServerSettings = show, connectionStatus = ConnectionTestStatus.Idle) }
    }

    fun testConnection(rawUrl: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionStatus = ConnectionTestStatus.Testing) }

            val trimmed = rawUrl.trim()
            val withScheme = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                if (trimmed.startsWith("localhost") || trimmed.startsWith("10.0.2.2") || trimmed.startsWith("127.0.0.1") || trimmed.startsWith("192.168.")) {
                    "http://$trimmed"
                } else {
                    "https://$trimmed"
                }
            } else {
                trimmed
            }
            val formatted = if (withScheme.endsWith("/")) withScheme else "$withScheme/"
            val probeUrl = "${formatted}api/me"

            withContext(Dispatchers.IO) {
                val start = System.currentTimeMillis()
                try {
                    val request = Request.Builder()
                        .url(probeUrl)
                        .get()
                        .build()

                    probeClient.newCall(request).execute().use { response ->
                        val duration = System.currentTimeMillis() - start
                        _uiState.update {
                            it.copy(
                                connectionStatus = ConnectionTestStatus.Success(
                                    responseTimeMs = duration,
                                    statusCode = response.code
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    val errorMsg = when (e) {
                        is java.net.ConnectException -> "Connection refused. Server is unreachable at this host/port."
                        is java.net.SocketTimeoutException -> "Timed out (5s). Server is taking too long to respond."
                        is java.net.UnknownHostException -> "Host name cannot be resolved."
                        else -> e.localizedMessage ?: "Connection failed"
                    }
                    _uiState.update {
                        it.copy(connectionStatus = ConnectionTestStatus.Failed(errorMsg))
                    }
                }
            }
        }
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
