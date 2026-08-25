package com.helpdesk.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.presentation.auth.LoginScreen
import com.helpdesk.app.presentation.dashboard.DashboardScreen
import com.helpdesk.app.presentation.tickets.detail.TicketDetailScreen
import com.helpdesk.app.presentation.tickets.list.TicketListScreen
import com.helpdesk.app.presentation.users.UsersScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val observeCurrentUserUseCase: ObserveCurrentUserUseCase = koinInject()
    val currentUser by observeCurrentUserUseCase().collectAsState(initial = null)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Tickets.route,
        "tickets",
        Screen.Users.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar && currentUser != null) {
                BottomNavBar(
                    navController = navController,
                    currentUser = currentUser
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = navController,
                startDestination = if (currentUser != null) Screen.Dashboard.route else Screen.Auth.route
            ) {
                composable(Screen.Auth.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        currentUser = currentUser,
                        onNavigateToTickets = { status ->
                            navController.navigate(Screen.Tickets.createRoute(status))
                        },
                        onCreateTicketClick = {
                            navController.navigate(Screen.Tickets.route)
                        },
                        onLogout = {
                            navController.navigate(Screen.Auth.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Screen.Tickets.route,
                    arguments = listOf(
                        navArgument("status") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val status = backStackEntry.arguments?.getString("status")
                    TicketListScreen(
                        initialStatus = status,
                        onTicketClick = { ticketId ->
                            navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                        }
                    )
                }

                composable(
                    route = Screen.TicketDetail.route,
                    arguments = listOf(
                        navArgument("ticketId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
                    TicketDetailScreen(
                        ticketId = ticketId,
                        currentUser = currentUser,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Users.route) {
                    UsersScreen()
                }
            }
        }
    }
}
