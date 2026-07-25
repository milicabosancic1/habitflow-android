package com.habitflow.app.recommendation

import androidx.room.withTransaction
import com.habitflow.app.data.local.AppDatabase
import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.HabitEntryDao
import com.habitflow.app.data.local.RecommendationDao
import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.rules.MakeEasierRule
import com.habitflow.app.recommendation.rules.NewHabitRule
import com.habitflow.app.recommendation.rules.OptimalTimeRule
import com.habitflow.app.recommendation.rules.StreakWarningRule
import com.habitflow.app.recommendation.rules.WeeklyInsightRule
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluira sva pravila preporuka i upisuje nove aktivne preporuke u Room.
 * Radi potpuno offline (bez mreže) — pokreće se preko WorkManager-a i pri otvaranju app-a.
 */
@Singleton
class RecommendationEngine @Inject constructor(
    private val database: AppDatabase,
    private val habitDao: HabitDao,
    private val entryDao: HabitEntryDao,
    private val recommendationDao: RecommendationDao,
    private val sessionManager: SessionManager
) {
    private val rules: List<RecommendationRule> = listOf(
        StreakWarningRule(), MakeEasierRule(), OptimalTimeRule(), NewHabitRule(), WeeklyInsightRule()
    )

    /**
     * Cela evaluacija ide u jednoj Room transakciji — sprečava race uslov kad
     * "on-launch" i periodični worker rade skoro istovremeno (svaki bi inače video
     * "ne postoji još" dedup-provere pre nego što drugi commit-uje svoj upis).
     */
    suspend fun run(): Result<Unit> = runCatching {
        database.withTransaction {
            evaluateAndUpsert()
            trim()
        }
    }

    private suspend fun evaluateAndUpsert() {
        val userId = sessionManager.currentUserId
        val habits = habitDao.getActive(userId)
        if (habits.isEmpty()) return

        val entriesByHabit = habits.associate { it.id to entryDao.getAllForHabit(it.id) }
        val context = RecommendationContext(
            userId = userId,
            today = LocalDate.now(),
            habits = habits,
            entriesByHabit = entriesByHabit
        )

        val existing = recommendationDao.getActiveOnce(userId).toMutableList()
        val candidates = rules.flatMap { it.evaluate(context) }

        var weeklyInsightReplaced = false
        for (candidate in candidates) {
            if (candidate.type == RecommendationType.WEEKLY_INSIGHT) {
                if (!weeklyInsightReplaced) {
                    existing.filter { it.type == RecommendationType.WEEKLY_INSIGHT }
                        .forEach { recommendationDao.dismiss(it.id) }
                    weeklyInsightReplaced = true
                }
                recommendationDao.upsert(candidate.toEntity(userId))
                continue
            }

            val alreadyActive = existing.any {
                it.type == candidate.type && it.habitId == candidate.habitId && it.category == candidate.category
            }
            if (alreadyActive) continue

            recommendationDao.upsert(candidate.toEntity(userId))
        }
    }

    private suspend fun trim() {
        val userId = sessionManager.currentUserId
        val active = recommendationDao.getActiveOnce(userId)
        if (active.size <= MAX_ACTIVE) return

        RecommendationPriority.sorted(active).drop(MAX_ACTIVE).forEach {
            recommendationDao.dismiss(it.id)
        }
    }

    private fun RecommendationCandidate.toEntity(userId: String): RecommendationEntity = RecommendationEntity(
        id = UUID.randomUUID().toString(),
        userId = userId,
        habitId = habitId,
        category = category,
        type = type,
        message = message,
        createdAt = System.currentTimeMillis()
    )

    companion object {
        private const val MAX_ACTIVE = 3
    }
}
