package com.habitflow.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Okida notifikaciju za jednu naviku — zakazan kao dnevni periodični posao po navici. */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val habitDao: HabitDao,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getString(KEY_HABIT_ID) ?: return Result.success()
        val habit = habitDao.getById(habitId) ?: return Result.success()

        // Odbrambena provera — navika je mogla biti arhivirana/izmenjena/vlasnik promenjen
        // između zakazivanja i okidanja.
        if (habit.isArchived || habit.reminderTime.isNullOrBlank()) return Result.success()
        if (habit.userId != sessionManager.currentUserId) return Result.success()

        ReminderNotificationHelper.show(context, habit)
        return Result.success()
    }

    companion object {
        const val KEY_HABIT_ID = "habitId"
    }
}
