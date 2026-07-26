package com.habitflow.app.ui

import androidx.lifecycle.ViewModel
import com.habitflow.app.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppGateViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {
    val hasOnboarded: StateFlow<Boolean> = sessionManager.hasOnboardedFlow
}
