package com.helpdesk.app.core.network

import android.content.Context
import android.content.SharedPreferences
import com.helpdesk.app.core.datastore.SessionManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

@Serializable
private data class SerializableCookie(
    val name: String,
    val value: String,
    val expiresAt: Long = 253402300799999L,
    val domain: String,
    val path: String = "/",
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val hostOnly: Boolean = false
) {
    fun toOkHttpCookie(): Cookie {
        val builder = Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(expiresAt)
            .path(path)

        if (hostOnly) {
            builder.hostOnlyDomain(domain)
        } else {
            builder.domain(domain)
        }
        if (secure) builder.secure()
        if (httpOnly) builder.httpOnly()
        return builder.build()
    }

    companion object {
        fun fromOkHttpCookie(cookie: Cookie): SerializableCookie {
            return SerializableCookie(
                name = cookie.name,
                value = cookie.value,
                expiresAt = cookie.expiresAt,
                domain = cookie.domain,
                path = cookie.path,
                secure = cookie.secure,
                httpOnly = cookie.httpOnly,
                hostOnly = cookie.hostOnly
            )
        }
    }
}

class SessionCookieJar(
    context: Context,
    private val sessionManager: SessionManager? = null
) : CookieJar {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("helpdesk_cookie_store", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    init {
        loadPersistedCookies()
    }

    private fun loadPersistedCookies() {
        try {
            val allEntries = prefs.all
            for ((host, jsonStr) in allEntries) {
                if (jsonStr is String && jsonStr.isNotBlank()) {
                    val list = json.decodeFromString<List<SerializableCookie>>(jsonStr)
                    val okList = list.map { it.toOkHttpCookie() }.toMutableList()
                    cookieStore[host] = okList
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun persistCookiesForHost(host: String, cookies: List<Cookie>) {
        try {
            val serializable = cookies.map { SerializableCookie.fromOkHttpCookie(it) }
            val jsonStr = json.encodeToString(serializable)
            prefs.edit().putString(host, jsonStr).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return

        val host = url.host
        val hostCookies = cookieStore.getOrPut(host) { mutableListOf() }

        synchronized(hostCookies) {
            for (newCookie in cookies) {
                hostCookies.removeAll { it.name == newCookie.name }
                hostCookies.add(newCookie)
            }
            persistCookiesForHost(host, hostCookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val hostCookies = cookieStore[host] ?: return emptyList()
        val now = System.currentTimeMillis()

        synchronized(hostCookies) {
            val validCookies = hostCookies.filter { it.expiresAt > now }
            if (validCookies.size != hostCookies.size) {
                hostCookies.clear()
                hostCookies.addAll(validCookies)
                persistCookiesForHost(host, hostCookies)
            }
            return validCookies
        }
    }

    fun clear() {
        cookieStore.clear()
        prefs.edit().clear().apply()
    }
}
