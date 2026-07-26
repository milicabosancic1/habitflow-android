package com.habitflow.app.achievement

import androidx.room.withTransaction
import com.habitflow.app.achievement.rules.ComebackRule
import com.habitflow.app.achievement.rules.FirstHabitRule
import com.habitflow.app.achievement.rules.PerfectWeekRule
import com.habitflow.app.achievement.rules.StreakRule
import com.habitflow.app.data.local.AchievementDao
import com.habitflow.app.data.local.AchievementEntity
import com.habitflow.app.data.local.AppDatabase
import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.HabitEntryDao
import com.habitflow.app.data.local.SessionManager
import com.habitflow.app.domain.AchievementType
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluira sva pravila bedževa i otključava nove. Radi potpuno offline —
 * pokreće se preko WorkManager-a i pri otvaranju app-a.
 */
@Singleton
class AchievementEngine @Inject constructor(
    private val database: AppDatabase,
    private val habitDao: HabitDao,
    private val entryDao: HabitEntryDao,
    private val achievementDao: AchievementDao,
    private val sessionManager: SessionManager
) {
    private val rules: List<AchievementRule> = listOf(
        FirstHabitRule(),
        StreakRule(AchievementType.STREAK_7, 7),
        StreakRule(AchievementType.STREAK_30, 30),
        PerfectWeekRule(),
        ComebackRule()
    )

    /** Transakcija od početka — sprečava da dva istovremena workera oba otključaju isti bedž. */
    suspend fun run(): Result<Unit> = runCatching {
        database.withTransaction { evaluateAndUnlock() }
    }

    private suspend fun evaluateAndUnlock() {
        val userId = sessionManager.currentUserId
        val habits = habitDao.getActive(userId)
        if (habits.isEmpty()) return

        val entriesByHabit = habits.associate { it.id to entryDao.getAllForHabit(it.id) }
        val context = AchievementContext(LocalDate.now(), habits, entriesByHabit)

        val alreadyUnlocked = achievementDao.getAllOnce(userId).map { it.type }.toSet()
        rules.filter { it.type !in alreadyUnlocked && it.isUnlocked(context) }
            .forEach {
                achievementDao.insert(
                    AchievementEntity(UUID.randomUUID().toString(), userId, it.type, System.currentTimeMillis())
                )
            }
    }
}
