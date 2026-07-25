package com.habitflow.app.recommendation.rules

import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.RecommendationCandidate
import com.habitflow.app.recommendation.RecommendationContext
import com.habitflow.app.recommendation.RecommendationRule

/** 4. zakon (never miss twice): upozorenje kad je niz upravo prekinut. */
class StreakWarningRule : RecommendationRule {
    override val type = RecommendationType.STREAK_WARNING

    override fun evaluate(context: RecommendationContext): List<RecommendationCandidate> {
        val yesterday = DateUtils.format(context.today.minusDays(1))
        val dayBefore = DateUtils.format(context.today.minusDays(2))
        val candidates = mutableListOf<RecommendationCandidate>()

        for (habit in context.habits) {
            val entries = context.entriesByHabit[habit.id].orEmpty()
            fun doneOn(date: String) = entries.any { it.date == date && it.status == EntryStatus.DONE }

            if (!doneOn(yesterday) && doneOn(dayBefore)) {
                candidates += RecommendationCandidate(
                    type = type,
                    habitId = habit.id,
                    message = "Juče si propustila '${habit.name}'. Ne propusti dva puta zaredom — " +
                        "danas je najvažniji dan!"
                )
            }
        }

        return candidates
    }
}
