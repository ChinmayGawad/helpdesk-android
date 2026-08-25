package com.helpdesk.app.core.di

import com.helpdesk.app.core.datastore.SessionManager
import com.helpdesk.app.core.network.DynamicHostInterceptor
import com.helpdesk.app.core.network.NetworkClient
import com.helpdesk.app.core.network.SessionCookieJar
import com.helpdesk.app.data.repository.AuthRepositoryImpl
import com.helpdesk.app.data.repository.TicketRepositoryImpl
import com.helpdesk.app.data.repository.UserRepositoryImpl
import com.helpdesk.app.domain.repository.AuthRepository
import com.helpdesk.app.domain.repository.TicketRepository
import com.helpdesk.app.domain.repository.UserRepository
import com.helpdesk.app.domain.usecase.auth.GetBaseUrlUseCase
import com.helpdesk.app.domain.usecase.auth.GetCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.LoginUseCase
import com.helpdesk.app.domain.usecase.auth.LogoutUseCase
import com.helpdesk.app.domain.usecase.auth.ObserveCurrentUserUseCase
import com.helpdesk.app.domain.usecase.auth.SetBaseUrlUseCase
import com.helpdesk.app.domain.usecase.ticket.CreateReplyUseCase
import com.helpdesk.app.domain.usecase.ticket.CreateTicketUseCase
import com.helpdesk.app.domain.usecase.ticket.GetDailyStatsUseCase
import com.helpdesk.app.domain.usecase.ticket.GetRepliesUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketDetailUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketStatsUseCase
import com.helpdesk.app.domain.usecase.ticket.GetTicketsUseCase
import com.helpdesk.app.domain.usecase.ticket.PolishReplyUseCase
import com.helpdesk.app.domain.usecase.ticket.SummarizeTicketUseCase
import com.helpdesk.app.domain.usecase.ticket.UpdateTicketUseCase
import com.helpdesk.app.domain.usecase.user.CreateUserUseCase
import com.helpdesk.app.domain.usecase.user.DeleteUserUseCase
import com.helpdesk.app.domain.usecase.user.GetUsersUseCase
import com.helpdesk.app.domain.usecase.user.UpdateUserUseCase
import com.helpdesk.app.presentation.auth.LoginViewModel
import com.helpdesk.app.presentation.dashboard.DashboardViewModel
import com.helpdesk.app.presentation.tickets.detail.TicketDetailViewModel
import com.helpdesk.app.presentation.tickets.list.TicketListViewModel
import com.helpdesk.app.presentation.users.UsersViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {
    single { SessionManager(androidContext()) }
    single { SessionCookieJar(get()) }
    single { DynamicHostInterceptor(get()) }
    single { NetworkClient.createOkHttpClient(get(), get()) }
    single { NetworkClient.createApiService(get(), get()) }
}

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<TicketRepository> { TicketRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val domainModule = module {
    // Auth
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveCurrentUserUseCase(get()) }
    factory { GetBaseUrlUseCase(get()) }
    factory { SetBaseUrlUseCase(get()) }

    // Ticket
    factory { GetTicketsUseCase(get()) }
    factory { GetTicketDetailUseCase(get()) }
    factory { CreateTicketUseCase(get()) }
    factory { UpdateTicketUseCase(get()) }
    factory { GetRepliesUseCase(get()) }
    factory { CreateReplyUseCase(get()) }
    factory { PolishReplyUseCase(get()) }
    factory { SummarizeTicketUseCase(get()) }
    factory { GetTicketStatsUseCase(get()) }
    factory { GetDailyStatsUseCase(get()) }

    // User
    factory { GetUsersUseCase(get()) }
    factory { CreateUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
}

val presentationModule = module {
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { TicketListViewModel(get(), get()) }
    viewModel { (ticketId: String) -> TicketDetailViewModel(ticketId, get(), get(), get(), get(), get(), get(), get()) }
    viewModel { UsersViewModel(get(), get(), get(), get(), get()) }
}

val appModules = listOf(coreModule, dataModule, domainModule, presentationModule)
