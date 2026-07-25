package com.habitflow.app.data.repository

import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.local.UserDao
import com.habitflow.app.data.local.UserEntity
import com.habitflow.app.data.remote.AuthApi
import com.habitflow.app.data.remote.LoginRequest
import com.habitflow.app.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val userDao: UserDao,
    private val habitDao: HabitDao
) {
    fun observeCurrentUser(): Flow<UserEntity?> = userDao.observeCurrentUser()

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        identityStatement: String
    ): Result<Unit> = runCatching {
        val res = authApi.register(RegisterRequest(email, password, displayName, identityStatement))
        onAuthSuccess(res.userId, res.token, email, displayName, identityStatement)
    }

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val res = authApi.login(LoginRequest(email, password))
        onAuthSuccess(res.userId, res.token, email, res.displayName ?: email, null)
    }

    suspend fun logout() {
        sessionManager.clearToken()
        userDao.clear()
    }

    private suspend fun onAuthSuccess(
        userId: String,
        token: String,
        email: String,
        displayName: String,
        identityStatement: String?
    ) {
        sessionManager.saveSession(token, userId)
        userDao.upsert(UserEntity(userId, email, displayName, identityStatement, System.currentTimeMillis()))
        habitDao.reassignOwner(SessionManager.LOCAL_USER_ID, userId)
    }
}
