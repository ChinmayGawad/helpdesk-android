package com.helpdesk.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReplyDto(
    val id: String,
    val content: String,
    val ticketId: String,
    val authorId: String,
    val senderType: String,
    val author: UserDto,
    val createdAt: String
)

@Serializable
data class ReplyListResponse(
    val replies: List<ReplyDto>
)

@Serializable
data class ReplyDetailResponse(
    val reply: ReplyDto
)

@Serializable
data class CreateReplyRequest(
    val content: String
)

@Serializable
data class PolishReplyRequest(
    val content: String
)

@Serializable
data class PolishReplyResponse(
    val text: String
)

@Serializable
data class SummarizeTicketResponse(
    val summary: String
)
