package com.helpdesk.app.core.network

import com.helpdesk.app.core.datastore.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class SessionCookieJar(
    private val sessionManager: SessionManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CookieJar {

    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val hostCookies = cookieStore.getOrPut(host) { mutableListOf() }
        
        synchronized(hostCookies) {
            for (newCookie in cookies) {
                hostCookies.removeAll { it.name == newCookie.name }
                hostCookies.add(newCookie)
            }
        }

        // Check if better-auth session token is in the cookies
        val sessionCookie = cookies.firstOrNull { it.name.contains("session_token", ignoreCase = true) }
        if (sessionCookie != null) {
            scope.launch {
                sessionManager.saveCookies("${sessionCookie.name}=${sessionCookie.value}")
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val hostCookies = cookieStore[url.host] ?: return emptyList()
        val now = System.currentTimeMillis()
        synchronized(hostCookies) {
            // Remove expired cookies
            hostCookies.removeAll { it.expiresAt < now }
            return hostCookies.toList()
        }
    }

    fun clear() {
        cookieStore.clear()
    }
}
