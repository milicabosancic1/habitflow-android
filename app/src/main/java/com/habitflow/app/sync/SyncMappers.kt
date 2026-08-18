package com.habitflow.app.sync

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.data.remote.HabitDto
import com.habitflow.app.data.remote.HabitEntryDto
import com.habitflow.app.domain.SyncStatus
import com.habitflow.app.domain.TrackingType

fun HabitEntity.toDto() = HabitDto(
    id = id,
    name = name,
    category = category,
    type = type,
    frequencyType = frequencyType,
    daysOfWeek = daysOfWeek,
    targetCount = targetCount,
    reminderTime = reminderTime,
    cueText = cueText,
    stackedAfterHabitId = stackedAfterHabitId,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt,
    trackingType = trackingType,
    unit = unit,
    incrementAmount = incrementAmount,
    color = color,
    weeklyTarget = weeklyTarget
)

fun HabitDto.toEntity(userId: String) = HabitEntity(
    id = id,
    userId = userId,
    name = name,
    category = category,
    type = type,
    frequencyType = frequencyType,
    daysOfWeek = daysOfWeek,
    targetCount = targetCount,
    reminderTime = reminderTime,
    cueText = cueText,
    stackedAfterHabitId = stackedAfterHabitId,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncStatus = SyncStatus.SYNCED,
    trackingType = trackingType ?: TrackingType.SIMPLE,
    unit = unit,
    incrementAmount = incrementAmount,
    color = color,
    weeklyTarget = weeklyTarget
)

fun HabitEntryEntity.toDto() = HabitEntryDto(
    id = id,
    habitId = habitId,
    date = date,
    status = status,
    value = value,
    updatedAt = updatedAt
)

fun HabitEntryDto.toEntity() = HabitEntryEntity(
    id = id,
    habitId = habitId,
    date = date,
    status = status,
    value = value,
    updatedAt = updatedAt,
    syncStatus = SyncStatus.SYNCED
)
