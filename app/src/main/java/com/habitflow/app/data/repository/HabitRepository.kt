@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.data.repository

import com.habitflow.app.data.local.*
import com.habitflow.app.domain.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Jedina tačka pristupa podacima o navikama.
 * Radi potpuno lokalno (Room); sinhronizacija sa serverom se dešava kroz SyncManager
 * kad je korisnik prijavljen, svaka izmena se već označava kao PENDING.
 */
@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val entryDao: HabitEntryDao,
    private val sessionManager: SessionManager
) {
    private val currentUserId: String get() = sessionManager.currentUserId

    fun observeActiveHabits(): Flow<List<HabitEntity>> =
        sessionManager.userIdFlow.flatMapLatest { userId -> habitDao.observeActive(userId) }

    fun observeHabit(id: String): Flow<HabitEntity?> = habitDao.observeById(id)

    fun observeEntriesForDate(date: String): Flow<List<HabitEntryEntity>> =
        entryDao.observeForDate(date)

    fun observeEntriesForHabit(habitId: String): Flow<List<HabitEntryEntity>> =
        entryDao.observeForHabit(habitId)

    suspend fun createHabit(
        name: String,
        category: String,
        type: HabitType,
        frequencyType: FrequencyType,
        daysOfWeek: String? = null,
        targetCount: Int = 1,
        reminderTime: String? = null,
        cueText: String? = null,
        stackedAfterHabitId: String? = null,
        trackingType: TrackingType = TrackingType.SIMPLE,
        unit: String? = null,
        incrementAmount: Int? = null,
        color: String? = null,
        weeklyTarget: Int? = null
    ) {
        val now = System.currentTimeMillis()
        habitDao.upsert(
            HabitEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                name = name,
                category = category,
                type = type,
                frequencyType = frequencyType,
                daysOfWeek = daysOfWeek,
                targetCount = targetCount,
                reminderTime = reminderTime,
                cueText = cueText,
                stackedAfterHabitId = stackedAfterHabitId,
                createdAt = now,
                updatedAt = now,
                syncStatus = SyncStatus.PENDING,
                trackingType = trackingType,
                unit = unit,
                incrementAmount = incrementAmount,
                color = color,
                weeklyTarget = weeklyTarget
            )
        )
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.upsert(
            habit.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncStatus.PENDING)
        )
    }

    suspend fun archiveHabit(habit: HabitEntity) {
        habitDao.upsert(
            habit.copy(
                isArchived = true,
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    /** Označava/menja status navike za dati datum (podrazumevano danas). */
    suspend fun toggleDone(habitId: String, date: String = DateUtils.today()) {
        val existing = entryDao.getForHabitAndDate(habitId, date)
        val now = System.currentTimeMillis()
        if (existing == null || existing.status != EntryStatus.DONE) {
            entryDao.upsert(
                HabitEntryEntity(
                    id = existing?.id ?: UUID.randomUUID().toString(),
                    habitId = habitId,
                    date = date,
                    status = EntryStatus.DONE,
                    value = 1,
                    updatedAt = now,
                    syncStatus = SyncStatus.PENDING
                )
            )
        } else {
            // Bila je DONE -> vrati na MISSED (poništavanje)
            entryDao.upsert(
                existing.copy(
                    status = EntryStatus.MISSED,
                    updatedAt = now,
                    syncStatus = SyncStatus.PENDING
                )
            )
        }
    }

    /** Upisuje apsolutnu vrednost (količina/broj) za dati datum; status se izvodi iz cilja navike. */
    suspend fun logValue(habitId: String, date: String = DateUtils.today(), value: Int) {
        val habit = habitDao.getById(habitId) ?: return
        val existing = entryDao.getForHabitAndDate(habitId, date)
        val status = QuantityStatus.statusFor(value, habit.targetCount)
        entryDao.upsert(
            HabitEntryEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                habitId = habitId,
                date = date,
                status = status,
                value = value.coerceAtLeast(0),
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    suspend fun getDoneDates(habitId: String): Set<String> =
        entryDao.getAllForHabit(habitId)
            .filter { it.status == EntryStatus.DONE }
            .map { it.date }
            .toSet()
}
