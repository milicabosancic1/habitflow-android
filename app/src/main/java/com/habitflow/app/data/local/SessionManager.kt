package com.habitflow.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Čuva JWT token i lokalnu sesiju. `userId` se NE briše na logout (samo token) —
 * inače bi navike pripojene nalogu postale nevidljive posle odjave.
 */
@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        const val LOCAL_USER_ID = "local-user"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LAST_SYNC = "last_sync_time"
    }

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "habitflow_session",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val token: String? get() = prefs.getString(KEY_TOKEN, null)
    val isLoggedIn: Boolean get() = token != null

    private val _userIdFlow = MutableStateFlow(prefs.getString(KEY_USER_ID, LOCAL_USER_ID) ?: LOCAL_USER_ID)
    val userIdFlow: StateFlow<String> = _userIdFlow.asStateFlow()
    val currentUserId: String get() = _userIdFlow.value

    var lastSyncTime: Long
        get() = prefs.getLong(KEY_LAST_SYNC, 0L)
        set(value) { prefs.edit().putLong(KEY_LAST_SYNC, value).apply() }

    fun saveSession(token: String, userId: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .apply()
        _userIdFlow.value = userId
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
