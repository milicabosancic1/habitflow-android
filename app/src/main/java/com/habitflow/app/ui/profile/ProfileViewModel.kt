package com.habitflow.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.local.UserEntity
import com.habitflow.app.data.repository.AuthRepository
import com.habitflow.app.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserEntity? = null,
    val lastSyncTime: Long = 0L,
    val syncing: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _syncing = MutableStateFlow(false)
    private val _lastSyncTime = MutableStateFlow(sessionManager.lastSyncTime)

    val uiState: StateFlow<ProfileUiState> =
        combine(authRepository.observeCurrentUser(), _syncing, _lastSyncTime) { user, syncing, lastSync ->
            ProfileUiState(user = user, lastSyncTime = lastSync, syncing = syncing)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    fun syncNow() {
        if (_syncing.value) return
        _syncing.value = true
        viewModelScope.launch {
            syncManager.sync()
            _lastSyncTime.value = sessionManager.lastSyncTime
            _syncing.value = false
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
