@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.data.repository

import com.habitflow.app.data.local.RecommendationDao
import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.data.local.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val recommendationDao: RecommendationDao,
    private val sessionManager: SessionManager
) {
    fun observeActive(): Flow<List<RecommendationEntity>> =
        sessionManager.userIdFlow.flatMapLatest { recommendationDao.observeActive(it) }

    suspend fun dismiss(id: String) = recommendationDao.dismiss(id)
}
