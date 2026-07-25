package com.habitflow.app.data.remote

import com.google.gson.annotations.SerializedName
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType

data class HabitDto(
    val id: String,
    val name: String,
    val category: String,
    val type: HabitType,
    val frequencyType: FrequencyType,
    val daysOfWeek: String?,
    val targetCount: Int,
    val reminderTime: String?,
    val cueText: String?,
    val stackedAfterHabitId: String?,
    // Backend (Jackson) serijalizuje boolean getter isArchived() kao JSON ključ "archived".
    @SerializedName("archived") val isArchived: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

data class HabitEntryDto(
    val id: String,
    val habitId: String,
    val date: String,
    val status: EntryStatus,
    val value: Int,
    val updatedAt: Long
)

data class SyncRequest(
    val since: Long,
    val habits: List<HabitDto>,
    val entries: List<HabitEntryDto>
)

data class SyncResponse(
    val serverTime: Long,
    val habits: List<HabitDto>,
    val entries: List<HabitEntryDto>
)
