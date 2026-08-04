@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.MonthlyCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val habits: List<HabitEntity> = emptyList(),
    val selectedHabitId: String? = null,
    val completionByDay: Map<LocalDate, Float> = emptyMap(),
    val loading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val monthFlow = MutableStateFlow(YearMonth.now())
    private val selectedHabitFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CalendarUiState> =
        combine(
            habitRepository.observeActiveHabits(),
            monthFlow,
            selectedHabitFlow
        ) { habits, month, selectedId -> Triple(habits, month, selectedId) }
            .flatMapLatest { (habits, month, selectedId) ->
                if (habits.isEmpty()) {
                    flowOf(
                        CalendarUiState(
                            month = month,
                            habits = habits,
                            selectedHabitId = selectedId,
                            completionByDay = emptyMap(),
                            loading = false
                        )
                    )
                } else {
                    combine(habits.map { habitRepository.observeEntriesForHabit(it.id) }) { entriesArrays ->
                        val entriesByHabit = habits.mapIndexed { i, h -> h.id to entriesArrays[i] }.toMap()
                        val relevantHabits = if (selectedId == null) habits else habits.filter { it.id == selectedId }
                        CalendarUiState(
                            month = month,
                            habits = habits,
                            selectedHabitId = selectedId,
                            completionByDay = MonthlyCalendar.completionByDay(relevantHabits, entriesByHabit, month),
                            loading = false
                        )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CalendarUiState()
            )

    fun previousMonth() {
        monthFlow.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        monthFlow.update { it.plusMonths(1) }
    }

    fun selectHabit(id: String?) {
        selectedHabitFlow.value = id
    }
}
