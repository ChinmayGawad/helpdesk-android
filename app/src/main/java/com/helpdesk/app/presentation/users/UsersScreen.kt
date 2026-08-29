package com.helpdesk.app.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.presentation.common.EmptyState
import com.helpdesk.app.presentation.common.ErrorBanner
import com.helpdesk.app.presentation.common.LoadingState
import com.helpdesk.app.presentation.common.ShimmerLoadingList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDark = isSystemInDarkTheme()

    val filteredUsers = uiState.users.filter { user ->
        if (uiState.searchQuery.isBlank()) true
        else user.name.contains(uiState.searchQuery, ignoreCase = true) ||
                user.email.contains(uiState.searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Team Management",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadUsers() }) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (currentUser?.role == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = { viewModel.openCreateDialog() },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add User", modifier = Modifier.size(24.dp))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search field Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                "Search team members by name or email...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        Icons.Outlined.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.errorMessage.isNullOrBlank()) {
                ErrorBanner(
                    message = uiState.errorMessage ?: "",
                    onRetry = { viewModel.loadUsers() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (currentUser != null && currentUser?.role != UserRole.ADMIN) {
                EmptyState(
                    title = "Access Restricted",
                    description = "Team member management and agent administration is restricted to Administrators.",
                    icon = Icons.Outlined.AdminPanelSettings
                )
            } else if (uiState.isLoading && uiState.users.isEmpty()) {
                ShimmerLoadingList(count = 5)
            } else if (filteredUsers.isEmpty()) {
                EmptyState(
                    title = "No team members found",
                    description = "No team members match your query. Add a new agent or administrator to collaborate.",
                    actionLabel = if (currentUser?.role == UserRole.ADMIN) "Add Team Member" else null,
                    onAction = { viewModel.openCreateDialog() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    items(filteredUsers, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            canDelete = user.role != UserRole.ADMIN && user.id != currentUser?.id,
                            onEdit = { viewModel.openEditDialog(user) },
                            onDelete = { viewModel.promptDelete(user) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Create / Edit Dialog
        if (uiState.showCreateEditDialog) {
            CreateEditUserDialog(
                userToEdit = uiState.selectedUserForEdit,
                isSaving = uiState.isSaving,
                errorMessage = uiState.saveErrorMessage,
                onDismiss = { viewModel.closeCreateEditDialog() },
                onSave = { name, email, password, role ->
                    viewModel.saveUser(name, email, password, role)
                }
            )
        }

        // Delete Confirmation Dialog
        if (uiState.showDeleteConfirmDialog && uiState.userToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDelete() },
                title = {
                    Text(
                        "Remove Team Member",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to remove ${uiState.userToDelete?.name} (${uiState.userToDelete?.email})? This action will revoke their active sessions and unassign any active tickets.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmDelete() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Delete User")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDelete() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun UserCard(
    user: User,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val isAdmin = user.role == UserRole.ADMIN
    val shape = RoundedCornerShape(14.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                shape
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (isDark) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // User Avatar initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isAdmin) {
                                Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF9333EA)))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF0EA5E9)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isAdmin) {
                                        if (isDark) Color(0xFF3B0764) else Color(0xFFF3E8FF)
                                    } else {
                                        if (isDark) Color(0xFF082F49) else Color(0xFFE0F2FE)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isAdmin) Color(0xFF9333EA).copy(alpha = 0.4f) else Color(0xFF0284C7).copy(alpha = 0.4f),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = user.role.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = if (isAdmin) {
                                    if (isDark) Color(0xFFD8B4FE) else Color(0xFF7E22CE)
                                } else {
                                    if (isDark) Color(0xFF7DD3FC) else Color(0xFF0369A1)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit User",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (canDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete User",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

