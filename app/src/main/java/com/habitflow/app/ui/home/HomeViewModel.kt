package com.habitflow.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.repository.AuthRepository
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.data.repository.RecommendationRepository
import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.recommendation.RecommendationPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val selectedDate: String = DateUtils.today(),
    val habits: List<HabitEntity> = emptyList(),
    val entriesById: Map<String, HabitEntryEntity> = emptyMap(),
    val topRecommendation: RecommendationEntity? = null,
    val displayName: String? = null,
    val subtitle: String = "Male navike, velike promene.",
    val loading: Boolean = true
) {
    val completedCount: Int get() = habits.count { entriesById[it.id]?.status == EntryStatus.DONE }
    val totalCount: Int get() = habits.size
    val progress: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
    val isToday: Boolean get() = selectedDate == DateUtils.today()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val recommendationRepository: RecommendationRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateUtils.today())

    private val entriesForSelectedDate = _selectedDate.flatMapLatest { date ->
        repository.observeEntriesForDate(date)
    }

    val uiState: StateFlow<HomeUiState> =
        combine(
            repository.observeActiveHabits(),
            entriesForSelectedDate,
            recommendationRepository.observeActive(),
            _selectedDate,
            authRepository.observeCurrentUser()
        ) { habits, entries, recommendations, date, user ->
            val entriesById = entries.associateBy { it.habitId }
            val identityStatement = sessionManager.identityStatement
            HomeUiState(
                selectedDate = date,
                habits = habits,
                entriesById = entriesById,
                topRecommendation = RecommendationPriority.sorted(recommendations).firstOrNull(),
                displayName = user?.displayName,
                subtitle = if (identityStatement.isNullOrBlank()) {
                    "Male navike, velike promene."
                } else {
                    "Želim da postanem osoba koja $identityStatement."
                },
                loading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun toggleDone(habitId: String) {
        viewModelScope.launch {
            repository.toggleDone(habitId, date = _selectedDate.value)
        }
    }

    fun logValue(habitId: String, value: Int) {
        viewModelScope.launch {
            repository.logValue(habitId, date = _selectedDate.value, value = value)
        }
    }

    fun dismissRecommendation(id: String) {
        viewModelScope.launch {
            recommendationRepository.dismiss(id)
        }
    }
}
