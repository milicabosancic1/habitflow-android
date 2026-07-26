@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.data.repository

import com.habitflow.app.data.local.AchievementDao
import com.habitflow.app.data.local.AchievementEntity
import com.habitflow.app.data.local.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao,
    private val sessionManager: SessionManager
) {
    fun observeAll(): Flow<List<AchievementEntity>> =
        sessionManager.userIdFlow.flatMapLatest { achievementDao.observeAll(it) }
}
