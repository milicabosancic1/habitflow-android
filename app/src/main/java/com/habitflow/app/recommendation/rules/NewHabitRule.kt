package com.habitflow.app.recommendation.rules

import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.RecommendationCandidate
import com.habitflow.app.recommendation.RecommendationContext
import com.habitflow.app.recommendation.RecommendationRule

/** 2. zakon (privlačnost): predlog nove navike u kategoriji gde korisnik već uspeva. */
class NewHabitRule : RecommendationRule {
    override val type = RecommendationType.NEW_HABIT

    override fun evaluate(context: RecommendationContext): List<RecommendationCandidate> {
        val qualifyingCategories = context.habits
            .groupBy { it.category }
            .filterValues { habitsInCategory ->
                habitsInCategory.any { habit ->
                    val entries = context.entriesByHabit[habit.id].orEmpty()
                    val total = entries.size
                    if (total < MIN_SAMPLES) return@any false
                    val done = entries.count { it.status == EntryStatus.DONE }
                    done.toFloat() / total > 0.8f
                }
            }
            .keys

        return qualifyingCategories.map { category ->
            RecommendationCandidate(
                type = type,
                category = category,
                message = "Odlično ti ide u kategoriji '$category'. Možda je vreme za novu naviku u istoj oblasti?"
            )
        }
    }

    companion object {
        private const val MIN_SAMPLES = 5
    }
}
