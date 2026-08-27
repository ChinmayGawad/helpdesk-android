package com.helpdesk.app.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.AiCardBgDark
import com.helpdesk.app.core.theme.AiCardBgLight
import com.helpdesk.app.core.theme.AiCardBorderDark
import com.helpdesk.app.core.theme.AiCardBorderLight
import com.helpdesk.app.core.theme.AiCardTextDark
import com.helpdesk.app.core.theme.AiCardTextLight
import com.helpdesk.app.core.theme.AiGradientEnd
import com.helpdesk.app.core.theme.AiGradientStart
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.core.theme.Secondary
import com.helpdesk.app.core.util.DateTimeUtils
import com.helpdesk.app.domain.model.DailyStat
import com.helpdesk.app.domain.model.TicketStats
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import com.helpdesk.app.presentation.common.ErrorBanner
import com.helpdesk.app.presentation.common.LoadingState
import com.helpdesk.app.presentation.common.StatCard
import java.util.Calendar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentUser: User?,
    onNavigateToTickets: (status: String?) -> Unit,
    onCreateTicketClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = remember(currentHour) {
        when (currentHour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Brush.linearGradient(listOf(AiGradientStart, AiGradientEnd))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDashboardData(isRefresh = true) }) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Header Welcome Card
            val isAdmin = currentUser?.role == UserRole.ADMIN
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
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(46.dp)
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
                                text = (currentUser?.name ?: "U").take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "$greeting, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Agent"}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentUser?.email ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

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
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentUser?.role?.displayName?.uppercase() ?: "AGENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.errorMessage.isNullOrBlank()) {
                ErrorBanner(
                    message = uiState.errorMessage ?: "",
                    onRetry = { viewModel.loadDashboardData() },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (uiState.isLoading && uiState.stats == null) {
                LoadingState(message = "Compiling live analytics & stats...")
            } else {
                val stats = uiState.stats

                // 2x2 Metric Cards Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Total Tickets",
                            value = "${stats?.totalTickets ?: 0}",
                            subtitle = "Tap to view all",
                            icon = Icons.Outlined.Inbox,
                            iconTint = Primary,
                            iconBg = if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                            onClick = { onNavigateToTickets(null) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Open Tickets",
                            value = "${stats?.openTickets ?: 0}",
                            subtitle = "Requires response",
                            icon = Icons.Outlined.HourglassEmpty,
                            iconTint = Color(0xFFEA580C),
                            iconBg = if (isDark) Color(0xFF451A03) else Color(0xFFFFEDD5),
                            onClick = { onNavigateToTickets("open") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "AI Auto-Resolved",
                            value = "${stats?.aiResolvedPercentage?.toInt() ?: 0}%",
                            subtitle = "${stats?.aiResolvedTickets ?: 0} automated fixes",
                            icon = Icons.Outlined.AutoAwesome,
                            iconTint = Color(0xFF9333EA),
                            iconBg = if (isDark) Color(0xFF3B0764) else Color(0xFFFAF5FF)
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Avg Resolution",
                            value = stats?.averageResolutionTime ?: "N/A",
                            subtitle = "Fast response time",
                            icon = Icons.Outlined.Speed,
                            iconTint = Color(0xFF16A34A),
                            iconBg = if (isDark) Color(0xFF052E16) else Color(0xFFDCFCE7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 30-Day Volume Trend Chart Section
                DailyVolumeChartCard(dailyStats = uiState.dailyStats)

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Navigation Shortcuts Hub
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onCreateTicketClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Ticket", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = { onNavigateToTickets("open") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Queue", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyVolumeChartCard(dailyStats: List<DailyStat>) {
    val isDark = isSystemInDarkTheme()
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "30-Day Ticket Volume",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap any bar to inspect daily metrics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (dailyStats.isNotEmpty()) {
                    val totalSum = dailyStats.sumOf { it.count }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Primary.copy(alpha = 0.12f))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$totalSum tickets",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Bar Tooltip
            AnimatedVisibility(
                visible = selectedIndex != null && selectedIndex in dailyStats.indices,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val stat = selectedIndex?.let { dailyStats.getOrNull(it) }
                if (stat != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅 ${DateTimeUtils.formatShortDate(stat.date)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${stat.count} ${if (stat.count == 1) "ticket" else "tickets"} created",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            )
                        }
                    }
                }
            }

            if (dailyStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No volume data available for the last 30 days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val maxCount = (dailyStats.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyStats.forEachIndexed { index, stat ->
                        val isSelected = selectedIndex == index
                        val barHeightFraction = (stat.count.toFloat() / maxCount).coerceIn(0.08f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedIndex = if (selectedIndex == index) null else index
                                }
                                .padding(horizontal = 2.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${stat.count}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((100 * barHeightFraction).dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (isSelected) {
                                            Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Primary))
                                        } else if (stat.count > 0) {
                                            Brush.verticalGradient(
                                                listOf(Primary.copy(alpha = 0.9f), Primary.copy(alpha = 0.6f))
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                )
                                            )
                                        }
                                    )
                                    .then(
                                        if (isSelected) Modifier.border(
                                            1.5.dp,
                                            Color(0xFF8B5CF6),
                                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        ) else Modifier
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = DateTimeUtils.formatShortDate(stat.date).take(6),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

