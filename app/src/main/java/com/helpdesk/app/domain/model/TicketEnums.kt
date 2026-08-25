package com.helpdesk.app.domain.model

data class Customer(
    val id: String,
    val name: String?,
    val email: String
)

enum class TicketStatus(val value: String, val label: String) {
    NEW("new", "New"),
    PROCESSING("processing", "Processing"),
    OPEN("open", "Open"),
    RESOLVED("resolved", "Resolved"),
    CLOSED("closed", "Closed");

    companion object {
        fun fromValue(value: String?): TicketStatus {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: OPEN
        }
    }
}

enum class TicketCategory(val value: String, val label: String) {
    GENERAL_QUESTION("general_question", "General Question"),
    TECHNICAL_QUESTION("technical_question", "Technical Question"),
    REFUND_REQUEST("refund_request", "Refund Request");

    companion object {
        fun fromValue(value: String?): TicketCategory {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: GENERAL_QUESTION
        }
    }
}

enum class TicketSource(val value: String, val label: String) {
    EMAIL("email", "Email"),
    WEB("web", "Web");

    companion object {
        fun fromValue(value: String?): TicketSource {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: WEB
        }
    }
}

enum class TicketSortColumn(val value: String, val label: String) {
    CREATED_AT("createdAt", "Date Created"),
    SUBJECT("subject", "Subject"),
    STATUS("status", "Status"),
    CATEGORY("category", "Category"),
    REQUESTER("requester", "Requester")
}

enum class TicketSortDirection(val value: String) {
    ASC("asc"),
    DESC("desc")
}
