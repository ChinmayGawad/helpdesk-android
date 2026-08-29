package com.helpdesk.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.helpdesk.app.domain.model.User
import com.helpdesk.app.domain.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "helpdesk_session")

class SessionManager(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val cachedBaseUrl = AtomicReference(DEFAULT_BASE_URL)
    private val cachedToken = AtomicReference<String?>(null)

    companion object {
        val KEY_BASE_URL = stringPreferencesKey("base_url")
        val KEY_USER_JSON = stringPreferencesKey("user_json")
        val KEY_SESSION_TOKEN = stringPreferencesKey("session_token")
        val KEY_COOKIES = stringPreferencesKey("session_cookies")
        const val DEFAULT_BASE_URL = "http://localhost:3000/"
    }

    val baseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BASE_URL] ?: DEFAULT_BASE_URL
    }

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val userJson = preferences[KEY_USER_JSON]
        if (!userJson.isNullOrBlank()) {
            try {
                val data = json.decodeFromString<CachedUser>(userJson)
                User(
                    id = data.id,
                    name = data.name,
                    email = data.email,
                    role = UserRole.fromValue(data.role),
                    createdAt = data.createdAt
                )
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    val sessionTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_SESSION_TOKEN]
    }

    val cookiesFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_COOKIES]
    }

    init {
        scope.launch {
            baseUrlFlow.collect { url ->
                cachedBaseUrl.set(url)
            }
        }
        scope.launch {
            sessionTokenFlow.collect { token ->
                cachedToken.set(token)
            }
        }
    }

    fun getCachedBaseUrl(): String = cachedBaseUrl.get()

    fun getCachedToken(): String? = cachedToken.get()

    suspend fun getBaseUrl(): String {
        return context.dataStore.data.map { it[KEY_BASE_URL] ?: DEFAULT_BASE_URL }.firstOrNull() ?: DEFAULT_BASE_URL
    }

    suspend fun getSessionToken(): String? {
        return context.dataStore.data.map { it[KEY_SESSION_TOKEN] }.firstOrNull()
    }

    suspend fun setBaseUrl(url: String) {
        val formatted = if (url.endsWith("/")) url else "$url/"
        cachedBaseUrl.set(formatted)
        context.dataStore.edit { preferences ->
            preferences[KEY_BASE_URL] = formatted
        }
    }

    suspend fun saveSession(user: User, token: String? = null) {
        if (!token.isNullOrBlank()) {
            cachedToken.set(token)
        }
        val cached = CachedUser(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role.value,
            createdAt = user.createdAt
        )
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_JSON] = json.encodeToString(cached)
            if (!token.isNullOrBlank()) {
                preferences[KEY_SESSION_TOKEN] = token
            }
        }
    }

    suspend fun saveCookies(cookiesHeader: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COOKIES] = cookiesHeader
        }
    }

    suspend fun clearSession() {
        cachedToken.set(null)
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_JSON)
            preferences.remove(KEY_SESSION_TOKEN)
            preferences.remove(KEY_COOKIES)
        }
    }
}

@kotlinx.serialization.Serializable
private data class CachedUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: String? = null
)
