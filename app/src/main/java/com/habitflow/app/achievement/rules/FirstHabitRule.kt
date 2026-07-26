package com.habitflow.app.achievement.rules

import com.habitflow.app.achievement.AchievementContext
import com.habitflow.app.achievement.AchievementRule
import com.habitflow.app.domain.AchievementType

/** 2. zakon (privlačnost): korisnik je kreirao svoju prvu naviku. */
class FirstHabitRule : AchievementRule {
    override val type = AchievementType.FIRST_HABIT

    override fun isUnlocked(context: AchievementContext): Boolean = context.habits.isNotEmpty()
}
