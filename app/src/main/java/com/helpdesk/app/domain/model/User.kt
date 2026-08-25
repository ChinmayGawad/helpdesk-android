package com.helpdesk.app.domain.model

enum class UserRole(val value: String, val displayName: String) {
    ADMIN("admin", "Admin"),
    AGENT("agent", "Agent");

    companion object {
        fun fromValue(value: String?): UserRole {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: AGENT
        }
    }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: String? = null
)
