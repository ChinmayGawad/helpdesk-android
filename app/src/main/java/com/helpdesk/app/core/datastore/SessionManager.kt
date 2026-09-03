/**
 * Session persistence: stores encrypted auth tokens, cookies, and user profile
 * in Android DataStore preferences, backed by the Android Keystore.
 */
package com.helpdesk.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.helpdesk.app.core.security.KeystoreCrypto
import com.helpdesk.app.core.util.BaseUrlNormalizer
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

/**
 * Persists and observes the user session (token, cookies, cached user) using
 * Android DataStore preferences. Sensitive values are encrypted with the
 * Android Keystore via [com.helpdesk.app.core.security.KeystoreCrypto].
 */
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
        const val DEFAULT_BASE_URL = com.helpdesk.app.BuildConfig.DEFAULT_API_BASE_URL
    }

    val baseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BASE_URL] ?: DEFAULT_BASE_URL
    }

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val encrypted = preferences[KEY_USER_JSON] ?: return@map null
        decodeUser(encrypted)
    }

    val sessionTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_SESSION_TOKEN]?.let { decodeToken(it) }
    }

    val cookiesFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_COOKIES]?.let { decodeCookies(it) }
    }

    init {
        scope.launch {
            baseUrlFlow.collect { url -> cachedBaseUrl.set(url) }
        }
        scope.launch {
            sessionTokenFlow.collect { token -> cachedToken.set(token) }
        }
    }

    fun getCachedBaseUrl(): String = cachedBaseUrl.get()

    fun getCachedToken(): String? = cachedToken.get()

    suspend fun getBaseUrl(): String {
        return context.dataStore.data.map { it[KEY_BASE_URL] ?: DEFAULT_BASE_URL }.firstOrNull() ?: DEFAULT_BASE_URL
    }

    suspend fun getSessionToken(): String? {
        return context.dataStore.data.map { it[KEY_SESSION_TOKEN]?.let { enc -> decodeToken(enc) } }.firstOrNull()
    }

    suspend fun setBaseUrl(url: String) {
        val formatted = BaseUrlNormalizer.normalize(url)
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
            preferences[KEY_USER_JSON] = requireNotNull(encryptUser(cached))
            if (!token.isNullOrBlank()) {
                preferences[KEY_SESSION_TOKEN] = requireNotNull(encryptToken(token))
            }
        }
    }

    suspend fun saveCookies(cookiesHeader: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COOKIES] = requireNotNull(encryptCookies(cookiesHeader))
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

    // ---- Encoding helpers ----

    private fun encodeUser(user: CachedUser): String? = runCatching { json.encodeToString(user) }.getOrNull()
    private fun decodeUser(encoded: String?): User? {
        if (encoded.isNullOrBlank()) return null
        return runCatching {
            val data = json.decodeFromString<CachedUser>(encoded)
            User(
                id = data.id,
                name = data.name,
                email = data.email,
                role = UserRole.fromValue(data.role),
                createdAt = data.createdAt
            )
        }.getOrNull()
    }

    private fun encodeToken(token: String): String? = KeystoreCrypto.encrypt(token)
    private fun decodeToken(encoded: String?): String? = KeystoreCrypto.decrypt(encoded)

    private fun encodeCookies(header: String): String? = KeystoreCrypto.encrypt(header)
    private fun decodeCookies(encoded: String?): String? = KeystoreCrypto.decrypt(encoded)

    private fun encryptUser(user: CachedUser): String? = encodeToken(json.encodeToString(user)!!)
    private fun encryptToken(token: String): String? = encodeToken(token)
    private fun encryptCookies(header: String): String? = encodeToken(header)
}

@kotlinx.serialization.Serializable
private data class CachedUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: String? = null
)