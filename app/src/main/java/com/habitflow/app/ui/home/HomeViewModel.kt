package com.habitflow.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val today: String = DateUtils.today(),
    val habits: List<HabitEntity> = emptyList(),
    val doneHabitIds: Set<String> = emptySet(),
    val loading: Boolean = true
) {
    val completedCount: Int get() = habits.count { doneHabitIds.contains(it.id) }
    val totalCount: Int get() = habits.size
    val progress: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val today = DateUtils.today()

    val uiState: StateFlow<HomeUiState> =
        combine(
            repository.observeActiveHabits(),
            repository.observeEntriesForDate(today)
        ) { habits, entries ->
            val doneIds = entries
                .filter { it.status == EntryStatus.DONE }
                .map { it.habitId }
                .toSet()
            HomeUiState(
                today = today,
                habits = habits,
                doneHabitIds = doneIds,
                loading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun toggleDone(habitId: String) {
        viewModelScope.launch {
            repository.toggleDone(habitId)
        }
    }
}
