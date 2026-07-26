package com.habitflow.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.habitflow.app.achievement.AchievementWorker
import com.habitflow.app.recommendation.RecommendationWorker
import com.habitflow.app.reminder.ReminderNotificationHelper
import com.habitflow.app.reminder.ReminderSchedulerWorker
import com.habitflow.app.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HabitFlowApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
        ReminderNotificationHelper.createChannel(this)
        scheduleSync()
        scheduleRecommendations()
        scheduleAchievements()
        scheduleReminders()
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniqueWork(
            "sync-on-launch",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(constraints).build()
        )
        workManager.enqueueUniquePeriodicWork(
            "sync-periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES).setConstraints(constraints).build()
        )
    }

    private fun scheduleRecommendations() {
        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniqueWork(
            "recommendations-on-launch",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<RecommendationWorker>().build()
        )
        workManager.enqueueUniquePeriodicWork(
            "recommendations-periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<RecommendationWorker>(24, TimeUnit.HOURS).build()
        )
    }

    private fun scheduleAchievements() {
        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniqueWork(
            "achievements-on-launch",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<AchievementWorker>().build()
        )
        workManager.enqueueUniquePeriodicWork(
            "achievements-periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<AchievementWorker>(24, TimeUnit.HOURS).build()
        )
    }

    private fun scheduleReminders() {
        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniqueWork(
            "reminders-on-launch",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<ReminderSchedulerWorker>().build()
        )
        workManager.enqueueUniquePeriodicWork(
            "reminders-periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ReminderSchedulerWorker>(6, TimeUnit.HOURS).build()
        )
    }
}
