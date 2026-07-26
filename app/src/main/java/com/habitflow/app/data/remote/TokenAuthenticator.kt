package com.habitflow.app.data.remote

import com.habitflow.app.BuildConfig
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.local.UserDao
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kad server vrati 401, pokušava obnavljanje preko /api/auth/refresh i ponavlja
 * originalni zahtev sa novim access tokenom. Refresh token se rotira pri svakoj
 * upotrebi (backend odmah opoziva stari) — zato sinhronizacija ispod sprečava
 * da dva istovremena 401 oba pozovu refresh sa istim, uskoro nevažećim tokenom.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : Authenticator {

    // Izolovan klijent BEZ ovog Authenticator-a na sebi — sprečava kružnu DI
    // zavisnost (AuthApi <- Retrofit <- OkHttpClient <- ovaj Authenticator) i
    // beskonačnu rekurziju ako i sam refresh poziv vrati 401.
    private val refreshApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("/api/auth/")) return null
        if (responseCount(response) >= 2) return null

        synchronized(lock) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = sessionManager.token

            // Neko drugi je (dok smo čekali na lock) već osvežio token — samo ponovi sa novim.
            if (currentToken != null && currentToken != failedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = sessionManager.refreshToken ?: return null

            return try {
                val result = runBlocking { refreshApi.refresh(RefreshRequest(refreshToken)) }
                sessionManager.updateTokens(result.token, result.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${result.token}")
                    .build()
            } catch (e: Exception) {
                runBlocking {
                    sessionManager.clearToken()
                    userDao.clear()
                }
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
