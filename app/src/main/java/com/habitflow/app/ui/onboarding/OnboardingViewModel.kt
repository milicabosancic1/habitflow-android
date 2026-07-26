package com.habitflow.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val step: Int = 0,
    val identityStatement: String = "",
    val habitName: String = "",
    val habitCategory: String = ""
)

const val ONBOARDING_STEP_COUNT = 4

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun next() {
        _uiState.update { it.copy(step = (it.step + 1).coerceAtMost(ONBOARDING_STEP_COUNT - 1)) }
    }

    fun back() {
        _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }

    fun onIdentityStatementChange(value: String) {
        _uiState.update { it.copy(identityStatement = value) }
    }

    fun onHabitNameChange(value: String) {
        _uiState.update { it.copy(habitName = value) }
    }

    fun onHabitCategoryChange(value: String) {
        _uiState.update { it.copy(habitCategory = value) }
    }

    fun finish() {
        val state = _uiState.value
        if (state.habitName.isBlank()) return
        viewModelScope.launch {
            sessionManager.identityStatement = state.identityStatement.trim().ifBlank { null }
            habitRepository.createHabit(
                name = state.habitName.trim(),
                category = state.habitCategory.trim().ifBlank { "Opšte" },
                type = HabitType.BUILD,
                frequencyType = FrequencyType.DAILY,
                targetCount = 1,
                reminderTime = null,
                cueText = null
            )
            sessionManager.setOnboarded()
        }
    }
}
