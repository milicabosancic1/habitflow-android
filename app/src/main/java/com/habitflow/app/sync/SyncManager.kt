package com.habitflow.app.sync

import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.HabitEntryDao
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.data.remote.SyncApi
import com.habitflow.app.data.remote.SyncRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Push lokalnih PENDING promena → pull novijih sa servera → upiši u Room.
 * Sve u jednom POST /api/sync pozivu (server interno primenjuje last-write-wins).
 */
@Singleton
class SyncManager @Inject constructor(
    private val syncApi: SyncApi,
    private val habitDao: HabitDao,
    private val entryDao: HabitEntryDao,
    private val sessionManager: SessionManager
) {
    suspend fun sync(): Result<Unit> {
        if (!sessionManager.isLoggedIn) return Result.success(Unit)

        return runCatching {
            val pendingHabits = habitDao.getPending()
            val pendingEntries = entryDao.getPending()

            val response = syncApi.sync(
                SyncRequest(
                    since = sessionManager.lastSyncTime,
                    habits = pendingHabits.map { it.toDto() },
                    entries = pendingEntries.map { it.toDto() }
                )
            )

            if (pendingHabits.isNotEmpty()) habitDao.markSynced(pendingHabits.map { it.id })
            if (pendingEntries.isNotEmpty()) entryDao.markSynced(pendingEntries.map { it.id })

            val userId = sessionManager.currentUserId
            response.habits.forEach { habitDao.upsert(it.toEntity(userId)) }
            response.entries.forEach { entryDao.upsert(it.toEntity()) }

            sessionManager.lastSyncTime = response.serverTime
        }
    }
}
