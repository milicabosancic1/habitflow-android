package com.habitflow.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.domain.ReminderScheduling
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Pomirljivi (reconciliation) worker — na svakom pokretanju otkače sve prethodno
 * zakazane podsetnike i ponovo zakaže za trenutno aktivne navike sa reminderTime-om.
 * Bezbedno za često ponavljanje: delayUntilNext() uvek cilja isti apsolutni trenutak.
 */
@HiltWorker
class ReminderSchedulerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val habitDao: HabitDao,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(REMINDER_TAG).result.get()

        val userId = sessionManager.currentUserId
        val habits = habitDao.getActive(userId).filter { !it.reminderTime.isNullOrBlank() }

        habits.forEach { habit ->
            val delay = ReminderScheduling.delayUntilNext(habit.reminderTime!!)
            workManager.enqueueUniquePeriodicWork(
                "reminder-${habit.id}",
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(ReminderWorker.KEY_HABIT_ID to habit.id))
                    .addTag(REMINDER_TAG)
                    .build()
            )
        }
        return Result.success()
    }

    companion object {
        const val REMINDER_TAG = "habit-reminder"
    }
}
