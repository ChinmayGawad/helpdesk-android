package com.helpdesk.app.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole

@Composable
fun BottomNavBar(
    navController: NavController,
    currentUser: User?
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = mutableListOf<BottomNavItem>(
        BottomNavItem.Dashboard,
        BottomNavItem.Tickets
    )

    if (currentUser?.role == UserRole.ADMIN) {
        items.add(BottomNavItem.Users)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.route?.startsWith(item.route.split("?")[0]) == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                selected = isSelected,
                onClick = {
                    val targetRoute = item.route.split("?")[0]
                    navController.navigate(targetRoute) {
                        popUpTo(targetRoute)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = Primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
