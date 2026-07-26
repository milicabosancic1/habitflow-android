package com.habitflow.app.achievement.rules

import com.habitflow.app.achievement.AchievementContext
import com.habitflow.app.achievement.AchievementRule
import com.habitflow.app.domain.AchievementType
import com.habitflow.app.domain.StatsAggregator
import java.time.DayOfWeek

/** 4. zakon (zadovoljstvo): sve navike završene svaki dan ove nedelje. */
class PerfectWeekRule : AchievementRule {
    override val type = AchievementType.PERFECT_WEEK

    override fun isUnlocked(context: AchievementContext): Boolean {
        if (context.today.dayOfWeek != DayOfWeek.SUNDAY || context.habits.isEmpty()) return false
        return StatsAggregator.completionPct(
            context.habits, context.entriesByHabit, context.today.minusDays(6), context.today
        ) == 100
    }
}
