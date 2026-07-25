package com.habitflow.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitflow.app.domain.*

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val category: String,
    val type: HabitType,
    val frequencyType: FrequencyType,
    val daysOfWeek: String? = null,
    val targetCount: Int = 1,
    val reminderTime: String? = null,
    val cueText: String? = null,
    val stackedAfterHabitId: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

@Entity(
    tableName = "habit_entries",
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitEntryEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: String, // YYYY-MM-DD
    val status: EntryStatus,
    val value: Int = 1,
    val updatedAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val habitId: String? = null,
    val category: String? = null,
    val type: RecommendationType,
    val message: String,
    val createdAt: Long,
    val isDismissed: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: AchievementType,
    val unlockedAt: Long
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val identityStatement: String? = null,
    val createdAt: Long
)
