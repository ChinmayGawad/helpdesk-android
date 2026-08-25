package com.helpdesk.app.core.network

import com.helpdesk.app.data.remote.dto.AuthResponse
import com.helpdesk.app.data.remote.dto.CreateReplyRequest
import com.helpdesk.app.data.remote.dto.CreateTicketRequest
import com.helpdesk.app.data.remote.dto.CreateUserRequest
import com.helpdesk.app.data.remote.dto.DailyStatDto
import com.helpdesk.app.data.remote.dto.DeleteUserResponse
import com.helpdesk.app.data.remote.dto.MeResponse
import com.helpdesk.app.data.remote.dto.PolishReplyRequest
import com.helpdesk.app.data.remote.dto.PolishReplyResponse
import com.helpdesk.app.data.remote.dto.ReplyDetailResponse
import com.helpdesk.app.data.remote.dto.ReplyListResponse
import com.helpdesk.app.data.remote.dto.SignInRequest
import com.helpdesk.app.data.remote.dto.StatsResponse
import com.helpdesk.app.data.remote.dto.SummarizeTicketResponse
import com.helpdesk.app.data.remote.dto.TicketDetailResponse
import com.helpdesk.app.data.remote.dto.TicketListResponse
import com.helpdesk.app.data.remote.dto.UpdateTicketRequest
import com.helpdesk.app.data.remote.dto.UpdateUserRequest
import com.helpdesk.app.data.remote.dto.UserDetailResponse
import com.helpdesk.app.data.remote.dto.UserListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HelpdeskApiService {

    // Auth
    @POST("api/auth/sign-in/email")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<AuthResponse>

    @POST("api/auth/sign-out")
    suspend fun signOut(): Response<Unit>

    @GET("api/me")
    suspend fun getMe(): Response<MeResponse>

    // Tickets
    @GET("api/tickets")
    suspend fun getTickets(
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
        @Query("source") source: String? = null,
        @Query("q") searchQuery: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortDir") sortDir: String? = null,
        @Query("page") page: Int? = 1,
        @Query("perPage") perPage: Int? = 20
    ): Response<TicketListResponse>

    @GET("api/tickets/{id}")
    suspend fun getTicket(
        @Path("id") id: String
    ): Response<TicketDetailResponse>

    @POST("api/tickets")
    suspend fun createTicket(
        @Body request: CreateTicketRequest
    ): Response<TicketDetailResponse>

    @PATCH("api/tickets/{id}")
    suspend fun updateTicket(
        @Path("id") id: String,
        @Body request: UpdateTicketRequest
    ): Response<TicketDetailResponse>

    // Replies & AI
    @GET("api/tickets/{id}/replies")
    suspend fun getReplies(
        @Path("id") ticketId: String
    ): Response<ReplyListResponse>

    @POST("api/tickets/{id}/replies")
    suspend fun createReply(
        @Path("id") ticketId: String,
        @Body request: CreateReplyRequest
    ): Response<ReplyDetailResponse>

    @POST("api/tickets/{id}/polish")
    suspend fun polishReply(
        @Path("id") ticketId: String,
        @Body request: PolishReplyRequest
    ): Response<PolishReplyResponse>

    @POST("api/tickets/{id}/summarize")
    suspend fun summarizeTicket(
        @Path("id") ticketId: String,
        @Body emptyBody: Map<String, String> = emptyMap()
    ): Response<SummarizeTicketResponse>

    // Stats
    @GET("api/tickets/stats")
    suspend fun getStats(): Response<StatsResponse>

    @GET("api/tickets/stats/daily")
    suspend fun getDailyStats(): Response<List<DailyStatDto>>

    // Users (Admin Only)
    @GET("api/users")
    suspend fun getUsers(): Response<UserListResponse>

    @POST("api/users")
    suspend fun createUser(
        @Body request: CreateUserRequest
    ): Response<UserDetailResponse>

    @PATCH("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): Response<UserDetailResponse>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): Response<DeleteUserResponse>
}
