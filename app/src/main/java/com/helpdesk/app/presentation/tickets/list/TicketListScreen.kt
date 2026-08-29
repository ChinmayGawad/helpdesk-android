package com.helpdesk.app.presentation.tickets.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.core.util.DateTimeUtils
import com.helpdesk.app.domain.model.Ticket
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketSortColumn
import com.helpdesk.app.domain.model.TicketSortDirection
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.presentation.common.CategoryBadge
import com.helpdesk.app.presentation.common.EmptyState
import com.helpdesk.app.presentation.common.ErrorBanner
import com.helpdesk.app.presentation.common.LoadingState
import com.helpdesk.app.presentation.common.ShimmerLoadingList
import com.helpdesk.app.presentation.common.StatusBadge
import androidx.compose.runtime.LaunchedEffect
import com.helpdesk.app.presentation.tickets.create.CreateTicketBottomSheet
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    onTicketClick: (String) -> Unit,
    initialStatus: String? = null,
    initialCreate: Boolean = false,
    viewModel: TicketListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(initialStatus) {
        if (!initialStatus.isNullOrBlank()) {
            viewModel.onStatusFilterSelect(TicketStatus.fromValue(initialStatus))
        }
    }

    LaunchedEffect(initialCreate) {
        if (initialCreate) {
            viewModel.toggleCreateDialog(true)
        }
    }

    val hasActiveFilters = uiState.selectedCategory != null ||
            uiState.selectedSource != null ||
            uiState.sortBy != TicketSortColumn.CREATED_AT ||
            uiState.sortDir != TicketSortDirection.DESC

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tickets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterSheet(true) }) {
                        BadgedBox(
                            badge = {
                                if (hasActiveFilters) {
                                    Badge(
                                        containerColor = Primary,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Tune,
                                contentDescription = "Filter & Sort",
                                tint = if (hasActiveFilters) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.loadTickets(isRefresh = true) }) {
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
            FloatingActionButton(
                onClick = { viewModel.toggleCreateDialog(true) },
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "New Ticket", modifier = Modifier.size(24.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                "Search subject, issue or customer...",
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
                                        contentDescription = "Clear search",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedStatus == null,
                            onClick = { viewModel.onStatusFilterSelect(null) },
                            label = { Text("All", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedStatus == TicketStatus.OPEN,
                            onClick = { viewModel.onStatusFilterSelect(if (uiState.selectedStatus == TicketStatus.OPEN) null else TicketStatus.OPEN) },
                            label = { Text("Open", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedStatus == TicketStatus.PROCESSING,
                            onClick = { viewModel.onStatusFilterSelect(if (uiState.selectedStatus == TicketStatus.PROCESSING) null else TicketStatus.PROCESSING) },
                            label = { Text("In Progress", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedStatus == TicketStatus.RESOLVED,
                            onClick = { viewModel.onStatusFilterSelect(if (uiState.selectedStatus == TicketStatus.RESOLVED) null else TicketStatus.RESOLVED) },
                            label = { Text("Resolved", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedStatus == TicketStatus.CLOSED,
                            onClick = { viewModel.onStatusFilterSelect(if (uiState.selectedStatus == TicketStatus.CLOSED) null else TicketStatus.CLOSED) },
                            label = { Text("Closed", style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.errorMessage.isNullOrBlank()) {
                ErrorBanner(
                    message = uiState.errorMessage ?: "",
                    onRetry = { viewModel.loadTickets() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (uiState.isLoading && uiState.tickets.isEmpty()) {
                ShimmerLoadingList(count = 5)
            } else if (uiState.tickets.isEmpty()) {
                EmptyState(
                    title = "No tickets found",
                    description = "No tickets match your query. Try clearing filters or creating a new support ticket.",
                    actionLabel = "Create Ticket",
                    onAction = { viewModel.toggleCreateDialog(true) }
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

                    items(uiState.tickets, key = { it.id }) { ticket ->
                        TicketItemCard(
                            ticket = ticket,
                            onClick = { onTicketClick(ticket.id) }
                        )
                    }

                    // Pagination Footer
                    item {
                        val pagination = uiState.pagination
                        if (pagination != null && pagination.totalPages > 1) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.3f else 0.6f),
                                        RoundedCornerShape(12.dp)
                                    ),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.loadTickets(page = uiState.currentPage - 1) },
                                        enabled = uiState.currentPage > 1,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Prev", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Text(
                                        text = "Page ${uiState.currentPage} of ${pagination.totalPages}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    OutlinedButton(
                                        onClick = { viewModel.loadTickets(page = uiState.currentPage + 1) },
                                        enabled = uiState.currentPage < pagination.totalPages,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Next", style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }

        // Create Ticket Bottom Sheet
        if (uiState.showCreateDialog) {
            CreateTicketBottomSheet(
                onDismiss = { viewModel.toggleCreateDialog(false) },
                onSubmit = { subject, desc, email ->
                    viewModel.createTicket(subject, desc, email) { created ->
                        onTicketClick(created.id)
                    }
                },
                isLoading = uiState.isCreatingTicket,
                errorMessage = uiState.createTicketError
            )
        }

        // Filter & Sort Bottom Sheet
        if (uiState.showFilterSheet) {
            FilterAndSortBottomSheet(
                currentSortBy = uiState.sortBy,
                currentSortDir = uiState.sortDir,
                currentCategory = uiState.selectedCategory,
                currentSource = uiState.selectedSource,
                onDismiss = { viewModel.toggleFilterSheet(false) },
                onApply = { sortBy, sortDir, cat, src ->
                    viewModel.onCategoryFilterSelect(cat)
                    viewModel.onSourceFilterSelect(src)
                    viewModel.onSortChange(sortBy, sortDir)
                }
            )
        }
    }
}

@Composable
fun TicketItemCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(14.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.65f),
                shape
            )
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (isDark) 2.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // Badges & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusBadge(status = ticket.status)
                    CategoryBadge(category = ticket.category)
                }

                Text(
                    text = DateTimeUtils.formatRelativeTime(ticket.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject
            Text(
                text = ticket.subject,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Description snippet
            if (ticket.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ticket.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer info: Requester & Assignee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = if (ticket.source == TicketSource.EMAIL) Icons.Outlined.Email else Icons.Outlined.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = ticket.requester.name ?: ticket.requester.email,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (ticket.assignee != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ticket.assignee.name.take(1).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = ticket.assignee.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterAndSortBottomSheet(
    currentSortBy: TicketSortColumn,
    currentSortDir: TicketSortDirection,
    currentCategory: TicketCategory?,
    currentSource: TicketSource?,
    onDismiss: () -> Unit,
    onApply: (TicketSortColumn, TicketSortDirection, TicketCategory?, TicketSource?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sortBy by remember { mutableStateOf(currentSortBy) }
    var sortDir by remember { mutableStateOf(currentSortDir) }
    var category by remember { mutableStateOf(currentCategory) }
    var source by remember { mutableStateOf(currentSource) }

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
                text = "Sort & Filter Options",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sort By",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    TicketSortColumn.CREATED_AT to "Date Created",
                    TicketSortColumn.SUBJECT to "Subject",
                    TicketSortColumn.STATUS to "Status"
                ).forEach { (col, label) ->
                    FilterChip(
                        selected = sortBy == col,
                        onClick = { sortBy = col },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = sortDir == TicketSortDirection.DESC,
                    onClick = { sortDir = TicketSortDirection.DESC },
                    label = { Text("Newest First", style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = sortDir == TicketSortDirection.ASC,
                    onClick = { sortDir = TicketSortDirection.ASC },
                    label = { Text("Oldest First", style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = category == null,
                    onClick = { category = null },
                    label = { Text("All", style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp)
                )
                TicketCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = if (category == cat) null else cat },
                        label = { Text(cat.label, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Submission Source",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = source == null,
                    onClick = { source = null },
                    label = { Text("All Sources", style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp)
                )
                TicketSource.entries.forEach { src ->
                    FilterChip(
                        selected = source == src,
                        onClick = { source = if (source == src) null else src },
                        label = { Text(src.label, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        sortBy = TicketSortColumn.CREATED_AT
                        sortDir = TicketSortDirection.DESC
                        category = null
                        source = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Reset All")
                }
                Button(
                    onClick = { onApply(sortBy, sortDir, category, source) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}


