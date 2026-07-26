package com.habitflow.app.achievement

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.AchievementType
import java.time.LocalDate

/** Zajednički interfejs za sva pravila otključavanja bedževa. */
interface AchievementRule {
    val type: AchievementType
    fun isUnlocked(context: AchievementContext): Boolean
}

data class AchievementContext(
    val today: LocalDate,
    val habits: List<HabitEntity>,
    val entriesByHabit: Map<String, List<HabitEntryEntity>>
)

fun AchievementContext.doneDatesFor(habitId: String): Set<String> =
    entriesByHabit[habitId].orEmpty().filter { it.status == EntryStatus.DONE }.map { it.date }.toSet()

fun AchievementContext.statusOn(habitId: String, date: LocalDate): EntryStatus? =
    entriesByHabit[habitId].orEmpty().find { it.date == DateUtils.format(date) }?.status
