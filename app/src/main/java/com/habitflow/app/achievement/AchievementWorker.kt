package com.habitflow.app.achievement

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AchievementWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val engine: AchievementEngine
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        engine.run().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
}
