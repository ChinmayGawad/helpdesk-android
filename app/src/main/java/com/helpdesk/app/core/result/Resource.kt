/**
 * Result wrapper for async operations: Success, Error, or Loading state.
 * Used throughout the app to represent the outcome of repository and use-case calls.
 */
package com.helpdesk.app.core.result

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val error: AppError) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> Resource.Error(error)
    is Resource.Loading -> Resource.Loading
}
