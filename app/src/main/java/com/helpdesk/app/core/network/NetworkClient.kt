package com.helpdesk.app.core.network

import com.helpdesk.app.core.datastore.SessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class DynamicHostInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val currentBaseUrl = runBlocking { sessionManager.getBaseUrl() }
        val newHttpUrl = currentBaseUrl.toHttpUrlOrNull()

        if (newHttpUrl != null) {
            val originalUrl = request.url
            val updatedUrl = originalUrl.newBuilder()
                .scheme(newHttpUrl.scheme)
                .host(newHttpUrl.host)
                .port(newHttpUrl.port)
                .build()
            request = request.newBuilder().url(updatedUrl).build()
        }

        return chain.proceed(request)
    }
}

class AuthInterceptor(
    private val sessionManager: SessionManager,
    private val sessionCookieJar: SessionCookieJar
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401 && !request.url.encodedPath.contains("sign-in")) {
            sessionCookieJar.clear()
            runBlocking {
                sessionManager.clearSession()
            }
        }

        return response
    }
}

object NetworkClient {

    fun createOkHttpClient(
        sessionCookieJar: SessionCookieJar,
        hostInterceptor: DynamicHostInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(sessionCookieJar)
            .addInterceptor(hostInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun createApiService(okHttpClient: OkHttpClient, sessionManager: SessionManager): HelpdeskApiService {
        val initialBaseUrl = runBlocking { sessionManager.getBaseUrl() }
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(initialBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(HelpdeskApiService::class.java)
    }
}
