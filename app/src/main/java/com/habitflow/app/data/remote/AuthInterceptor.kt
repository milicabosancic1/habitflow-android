package com.habitflow.app.data.remote

import com.habitflow.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Dodaje `Authorization: Bearer <token>` na sve rute osim onih pod /api/auth/. */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath.contains("/api/auth/")) {
            return chain.proceed(request)
        }
        val token = sessionManager.token ?: return chain.proceed(request)
        return chain.proceed(
            request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        )
    }
}
