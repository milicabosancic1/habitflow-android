package com.habitflow.app.ui.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    fun save(
        name: String,
        category: String,
        type: HabitType,
        frequencyType: FrequencyType,
        targetCount: Int,
        reminderTime: String?,
        cueText: String?,
        onSaved: () -> Unit
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createHabit(
                name = name.trim(),
                category = category.ifBlank { "Opšte" },
                type = type,
                frequencyType = frequencyType,
                targetCount = targetCount,
                reminderTime = reminderTime?.ifBlank { null },
                cueText = cueText?.ifBlank { null }
            )
            onSaved()
        }
    }
}
