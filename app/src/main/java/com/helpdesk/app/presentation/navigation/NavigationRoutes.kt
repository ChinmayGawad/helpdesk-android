package com.helpdesk.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Dashboard : Screen("dashboard")
    data object Tickets : Screen("tickets?status={status}&create={create}") {
        fun createRoute(status: String? = null, create: Boolean = false): String {
            val params = mutableListOf<String>()
            if (!status.isNullOrBlank()) params.add("status=$status")
            if (create) params.add("create=true")
            return if (params.isEmpty()) "tickets" else "tickets?" + params.joinToString("&")
        }
    }
    data object TicketDetail : Screen("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
    data object Users : Screen("users")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        title = "Dashboard",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Tickets : BottomNavItem(
        route = "tickets",
        title = "Tickets",
        selectedIcon = Icons.AutoMirrored.Filled.ListAlt,
        unselectedIcon = Icons.AutoMirrored.Outlined.ListAlt
    )
    data object Users : BottomNavItem(
        route = Screen.Users.route,
        title = "Team",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
