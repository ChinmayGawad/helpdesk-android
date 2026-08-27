package com.helpdesk.app.presentation.tickets.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.core.util.DateTimeUtils
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.presentation.common.CategoryBadge
import com.helpdesk.app.presentation.common.ErrorBanner
import com.helpdesk.app.presentation.common.LoadingState
import com.helpdesk.app.presentation.common.StatusBadge
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    currentUser: User?,
    onBackClick: () -> Unit,
    viewModel: TicketDetailViewModel = koinViewModel(parameters = { parametersOf(ticketId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val ticket = uiState.ticket
    val isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ticket #${ticketId.take(8)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        if (ticket != null) {
                            Text(
                                text = ticket.category.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadTicketData()
                        viewModel.loadReplies()
                    }) {
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
        bottomBar = {
            ReplyComposer(
                draftReply = uiState.draftReply,
                onDraftChange = { viewModel.onDraftChange(it) },
                onSendClick = { viewModel.sendReply() },
                onPolishClick = { viewModel.polishDraftReply() },
                isSending = uiState.isSendingReply,
                isPolishing = uiState.isPolishingReply
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoadingTicket && ticket == null) {
            LoadingState(message = "Loading ticket details...")
        } else if (ticket == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                ErrorBanner(
                    message = uiState.errorMessage ?: "Ticket not found",
                    onRetry = { viewModel.loadTicketData() }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Ticket Subject & Status Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                                RoundedCornerShape(16.dp)
                            ),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isDark) 2.dp else 0.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Status Badge with Dropdown Affordance
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.Transparent,
                                        modifier = Modifier.clickable { viewModel.toggleStatusSheet(true) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            StatusBadge(status = ticket.status)
                                            Icon(
                                                imageVector = Icons.Outlined.ArrowDropDown,
                                                contentDescription = "Change status",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    CategoryBadge(category = ticket.category)
                                }

                                Text(
                                    text = DateTimeUtils.formatRelativeTime(ticket.createdAt),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = ticket.subject,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (ticket.source == TicketSource.EMAIL) Icons.Outlined.Email else Icons.Outlined.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Submitted via ${ticket.source.label}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Requester & Assignee Meta Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Requester Info Card
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                                    RoundedCornerShape(14.dp)
                                ),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = if (isDark) 2.dp else 0.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "REQUESTER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ticket.requester.name ?: "Customer",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = ticket.requester.email,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Assignee Card
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable(enabled = currentUser?.role == UserRole.ADMIN) {
                                    viewModel.toggleAssigneeSheet(true)
                                },
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = if (isDark) 2.dp else 0.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ASSIGNEE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (currentUser?.role == UserRole.ADMIN) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit Assignee",
                                            modifier = Modifier.size(13.dp),
                                            tint = Primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ticket.assignee?.name ?: "Unassigned",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = ticket.assignee?.email ?: if (currentUser?.role == UserRole.ADMIN) "Tap to assign" else "No agent assigned",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = if (ticket.assignee == null && currentUser?.role == UserRole.ADMIN) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // AI Summary Card
                item {
                    AISummaryCard(
                        summary = uiState.aiSummary,
                        isLoading = uiState.isSummarizing,
                        onGenerateClick = { viewModel.generateAISummary() }
                    )
                }

                // Issue Description Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                                RoundedCornerShape(14.dp)
                            ),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isDark) 2.dp else 0.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Original Description",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ticket.description.ifBlank { "No description provided." },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp,
                                    fontSize = 13.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                // Conversation / Replies Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Forum,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Conversation Thread",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${uiState.replies.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Replies List
                if (uiState.isLoadingReplies && uiState.replies.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                } else if (uiState.replies.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(16.dp),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = "💬 No replies yet. Type a response below to start the conversation with the customer.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(uiState.replies, key = { it.id }) { reply ->
                        ReplyBubble(reply = reply)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // AI Polish Preview Modal
        if (uiState.showPolishPreviewDialog && !uiState.polishedReply.isNullOrBlank()) {
            PolishPreviewDialog(
                originalText = uiState.draftReply,
                polishedText = uiState.polishedReply ?: "",
                onAccept = { viewModel.applyPolishedReply() },
                onDismiss = { viewModel.dismissPolishPreview() }
            )
        }

        // Status Change Sheet
        if (uiState.showStatusSheet) {
            StatusChangeBottomSheet(
                currentStatus = ticket?.status ?: TicketStatus.OPEN,
                onDismiss = { viewModel.toggleStatusSheet(false) },
                onSelectStatus = { viewModel.updateStatus(it) }
            )
        }

        // Assignee Selection Sheet (Admin)
        if (uiState.showAssigneeSheet) {
            AssigneeSelectionBottomSheet(
                currentAssigneeId = ticket?.assignee?.id,
                agents = uiState.availableAgents,
                onDismiss = { viewModel.toggleAssigneeSheet(false) },
                onSelectAssignee = { viewModel.updateAssignee(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusChangeBottomSheet(
    currentStatus: TicketStatus,
    onDismiss: () -> Unit,
    onSelectStatus: (TicketStatus) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Change Ticket Status",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TicketStatus.entries.forEach { status ->
                val isSelected = status == currentStatus
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Primary.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { onSelectStatus(status) }
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusBadge(status = status)
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssigneeSelectionBottomSheet(
    currentAssigneeId: String?,
    agents: List<User>,
    onDismiss: () -> Unit,
    onSelectAssignee: (String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Assign Ticket to Agent",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Unassigned option
            val isUnassigned = currentAssigneeId == null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isUnassigned) Primary.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onSelectAssignee(null) }
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Unassigned",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isUnassigned) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isUnassigned) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            agents.filter { it.role == UserRole.AGENT }.forEach { agent ->
                val isSelected = agent.id == currentAssigneeId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Primary.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { onSelectAssignee(agent.id) }
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = agent.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = agent.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

