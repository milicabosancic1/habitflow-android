package com.habitflow.app.achievement.rules

import com.habitflow.app.achievement.AchievementContext
import com.habitflow.app.achievement.AchievementRule
import com.habitflow.app.achievement.doneDatesFor
import com.habitflow.app.domain.AchievementType
import com.habitflow.app.domain.StreakCalculator

/** Pokriva STREAK_7 i STREAK_30 — ista logika, različit prag. */
class StreakRule(
    override val type: AchievementType,
    private val threshold: Int
) : AchievementRule {

    override fun isUnlocked(context: AchievementContext): Boolean =
        context.habits.any { StreakCalculator.currentStreak(context.doneDatesFor(it.id)) >= threshold }
}
