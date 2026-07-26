package com.habitflow.app.achievement.rules

import com.habitflow.app.achievement.AchievementContext
import com.habitflow.app.achievement.AchievementRule
import com.habitflow.app.achievement.statusOn
import com.habitflow.app.domain.AchievementType
import com.habitflow.app.domain.EntryStatus

/** 4. zakon (never miss twice, obrnuto): povratak nakon nekoliko propuštenih dana. */
class ComebackRule : AchievementRule {
    override val type = AchievementType.COMEBACK

    override fun isUnlocked(context: AchievementContext): Boolean = context.habits.any { habit ->
        context.statusOn(habit.id, context.today) == EntryStatus.DONE &&
            (1..3).all { context.statusOn(habit.id, context.today.minusDays(it.toLong())) != EntryStatus.DONE }
    }
}
