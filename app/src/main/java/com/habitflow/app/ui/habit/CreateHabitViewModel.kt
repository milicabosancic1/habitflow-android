package com.habitflow.app.ui.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType
import com.habitflow.app.domain.TrackingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: String? = savedStateHandle.get<String>("habitId")
    val isEditing: Boolean get() = habitId != null

    /** Navika koja se izmenjuje — null dok se ne učita, ili trajno null u modu kreiranja. */
    val existingHabit: StateFlow<HabitEntity?> = habitId
        ?.let { repository.observeHabit(it).stateIn(viewModelScope, SharingStarted.Eagerly, null) }
        ?: MutableStateFlow(null)

    /** Ostale aktivne navike za habit stacking izbor (isključuje naviku koja se trenutno izmenjuje). */
    val stackableHabits: StateFlow<List<HabitEntity>> =
        repository.observeActiveHabits()
            .map { habits -> habits.filter { it.id != habitId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun save(
        name: String,
        category: String,
        type: HabitType,
        frequencyType: FrequencyType,
        daysOfWeek: String?,
        targetCount: Int,
        reminderTime: String?,
        cueText: String?,
        stackedAfterHabitId: String?,
        trackingType: TrackingType = TrackingType.SIMPLE,
        unit: String? = null,
        incrementAmount: Int? = null,
        color: String? = null,
        weeklyTarget: Int? = null,
        onSaved: () -> Unit
    ) {
        if (name.isBlank()) return
        val current = existingHabit.value
        viewModelScope.launch {
            if (current != null) {
                repository.updateHabit(
                    current.copy(
                        name = name.trim(),
                        category = category.ifBlank { "Opšte" },
                        type = type,
                        frequencyType = frequencyType,
                        daysOfWeek = daysOfWeek,
                        targetCount = targetCount,
                        reminderTime = reminderTime?.ifBlank { null },
                        cueText = cueText?.ifBlank { null },
                        stackedAfterHabitId = stackedAfterHabitId,
                        trackingType = trackingType,
                        unit = unit?.ifBlank { null },
                        incrementAmount = incrementAmount,
                        color = color,
                        weeklyTarget = weeklyTarget
                    )
                )
            } else {
                repository.createHabit(
                    name = name.trim(),
                    category = category.ifBlank { "Opšte" },
                    type = type,
                    frequencyType = frequencyType,
                    daysOfWeek = daysOfWeek,
                    targetCount = targetCount,
                    reminderTime = reminderTime?.ifBlank { null },
                    cueText = cueText?.ifBlank { null },
                    stackedAfterHabitId = stackedAfterHabitId,
                    trackingType = trackingType,
                    unit = unit?.ifBlank { null },
                    incrementAmount = incrementAmount,
                    color = color,
                    weeklyTarget = weeklyTarget
                )
            }
            onSaved()
        }
    }
}
