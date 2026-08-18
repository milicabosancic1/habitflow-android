@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.ui.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitDetailUiState(
    val habit: HabitEntity? = null,
    val stackedAfterHabitName: String? = null,
    val doneDates: Set<String> = emptySet(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDone: Int = 0,
    val loading: Boolean = true
)

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: String = savedStateHandle.get<String>("habitId") ?: ""

    val uiState: StateFlow<HabitDetailUiState> =
        repository.observeHabit(habitId).flatMapLatest { habit ->
            val stackedHabitFlow = habit?.stackedAfterHabitId
                ?.let { repository.observeHabit(it) }
                ?: flowOf(null)

            combine(
                repository.observeEntriesForHabit(habitId),
                stackedHabitFlow
            ) { entries, stackedHabit ->
                val doneDates = entries
                    .filter { it.status == com.habitflow.app.domain.EntryStatus.DONE }
                    .map { it.date }
                    .toSet()
                HabitDetailUiState(
                    habit = habit,
                    stackedAfterHabitName = stackedHabit?.name,
                    doneDates = doneDates,
                    currentStreak = StreakCalculator.currentStreak(doneDates),
                    longestStreak = StreakCalculator.longestStreak(doneDates),
                    totalDone = doneDates.size,
                    loading = false
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HabitDetailUiState()
        )

    fun archive(onDone: () -> Unit) {
        val habit = uiState.value.habit ?: return
        viewModelScope.launch {
            repository.archiveHabit(habit)
            onDone()
        }
    }
}
