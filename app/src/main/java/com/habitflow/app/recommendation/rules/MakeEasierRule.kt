package com.habitflow.app.recommendation.rules

import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.RecommendationCandidate
import com.habitflow.app.recommendation.RecommendationContext
import com.habitflow.app.recommendation.RecommendationRule

/** 3. zakon (lakoća, pravilo 2 minuta): olakšaj naviku koja izmiče. */
class MakeEasierRule : RecommendationRule {
    override val type = RecommendationType.MAKE_EASIER

    override fun evaluate(context: RecommendationContext): List<RecommendationCandidate> {
        val windowStart = context.today.minusDays(6)
        val candidates = mutableListOf<RecommendationCandidate>()

        for (habit in context.habits) {
            val missedCount = context.entriesByHabit[habit.id]
                .orEmpty()
                .count { entry ->
                    entry.status == EntryStatus.MISSED &&
                        DateUtils.parse(entry.date).let { it >= windowStart && it <= context.today }
                }
            if (missedCount < 3) continue

            candidates += RecommendationCandidate(
                type = type,
                habitId = habit.id,
                message = "'${habit.name}' ti izmiče ove nedelje. Probaj pravilo 2 minuta — " +
                    "smanji cilj na najmanju moguću verziju."
            )
        }

        return candidates
    }
}
