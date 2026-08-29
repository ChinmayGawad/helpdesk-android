package com.helpdesk.app.core.result

sealed interface AppError {
    data class Network(val message: String) : AppError
    data class Server(val code: Int, val message: String) : AppError
    data class Validation(val message: String) : AppError
    data object Unauthorized : AppError
    data class Forbidden(val message: String = "Permission denied. Admin role required.") : AppError
    data class Unknown(val message: String) : AppError
}

fun AppError.toUserMessage(): String = when (this) {
    is AppError.Network -> message.ifBlank { "Network connection error. Please check your internet." }
    is AppError.Server -> message.ifBlank { "Server returned error ($code)" }
    is AppError.Validation -> message
    is AppError.Unauthorized -> "Session expired or unauthorized. Please sign in again."
    is AppError.Forbidden -> message.ifBlank { "Permission denied. Admin role required." }
    is AppError.Unknown -> message.ifBlank { "An unexpected error occurred." }
}
