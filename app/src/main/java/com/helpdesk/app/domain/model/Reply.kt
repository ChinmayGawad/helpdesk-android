package com.helpdesk.app.domain.model

enum class ReplySenderType(val value: String) {
    AGENT("agent"),
    CUSTOMER("customer");

    companion object {
        fun fromValue(value: String?): ReplySenderType {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: AGENT
        }
    }
}

data class Reply(
    val id: String,
    val content: String,
    val ticketId: String,
    val authorId: String,
    val senderType: ReplySenderType,
    val author: User,
    val createdAt: String
)
