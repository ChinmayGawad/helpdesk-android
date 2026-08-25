package com.helpdesk.app.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpdesk.app.core.result.Resource
import com.helpdesk.app.core.result.toUserMessage
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.domain.usecase.user.CreateUserUseCase
import com.helpdesk.app.domain.usecase.user.DeleteUserUseCase
import com.helpdesk.app.domain.usecase.user.GetUsersUseCase
import com.helpdesk.app.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val saveErrorMessage: String? = null,
    val selectedUserForEdit: User? = null,
    val showCreateEditDialog: Boolean = false,
    val userToDelete: User? = null,
    val showDeleteConfirmDialog: Boolean = false
)

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = observeCurrentUserUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getUsersUseCase()
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(users = result.data, isLoading = false) }
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openCreateDialog() {
        _uiState.update {
            it.copy(
                selectedUserForEdit = null,
                showCreateEditDialog = true,
                saveErrorMessage = null
            )
        }
    }

    fun openEditDialog(user: User) {
        _uiState.update {
            it.copy(
                selectedUserForEdit = user,
                showCreateEditDialog = true,
                saveErrorMessage = null
            )
        }
    }

    fun closeCreateEditDialog() {
        _uiState.update {
            it.copy(
                selectedUserForEdit = null,
                showCreateEditDialog = false,
                saveErrorMessage = null
            )
        }
    }

    fun promptDelete(user: User) {
        _uiState.update { it.copy(userToDelete = user, showDeleteConfirmDialog = true) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(userToDelete = null, showDeleteConfirmDialog = false) }
    }

    fun saveUser(name: String, email: String, password: String, role: UserRole) {
        val userToEdit = _uiState.value.selectedUserForEdit
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            val result = if (userToEdit == null) {
                createUserUseCase(name, email, password, role)
            } else {
                updateUserUseCase(userToEdit.id, name, email, password.ifBlank { null }, role)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            showCreateEditDialog = false,
                            selectedUserForEdit = null
                        )
                    }
                    loadUsers()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveErrorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun confirmDelete() {
        val user = _uiState.value.userToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            val result = deleteUserUseCase(user.id)
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            userToDelete = null,
                            showDeleteConfirmDialog = false
                        )
                    }
                    loadUsers()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
